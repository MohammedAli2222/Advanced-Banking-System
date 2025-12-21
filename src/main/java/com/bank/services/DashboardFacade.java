package com.bank.services;

import com.bank.core.Account;
import com.bank.notifications.Observer;
import com.bank.notifications.TransactionLogger;
import com.bank.recommendations.RecommendationEngine;
import com.bank.recommendations.SpendingStrategy;
import com.bank.reports.ReportComponent;
import com.bank.security.AccessControlManager;
import com.bank.security.AuthenticationService;
import com.bank.support.SupportHandler;
import com.bank.support.Ticket;
import com.bank.transactions.RecurringTransaction;
import com.bank.transactions.Transaction;
import com.bank.transactions.TransactionExecutor;
import com.bank.transactions.TransactionHandler;
import com.bank.utils.AccountEvent;
import com.bank.utils.Money;
import com.bank.utils.Schedule;
import com.bank.utils.TransactionType;
import com.bank.customers.Customer;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.*;

/**
 * DashboardFacade — high-level API that hides complex internal subsystems:
 * - account creation and notifications
 * - transaction processing (async via TransactionExecutor)
 * - recurring transactions management
 * - reporting, support, recommendations
 *
 * This version integrates TransactionExecutor for concurrent processing and
 * maintains a registry of recurring jobs for cancellation/listing.
 */
public class DashboardFacade {
    private final Customer customer;
    private final TransactionHandler transactionChain;
    private final SupportHandler supportChain;
    private final TransactionLogger transactionLogger;
    private final RecommendationEngine recommendationEngine;
    private final AccessControlManager accessControl;
    private final AuthenticationService authService;
    private final TransactionExecutor txnExecutor;

    // Map to keep track of recurring jobs so they can be cancelled or listed
    private final Map<String, RecurringTransaction> recurringJobs = new ConcurrentHashMap<>();

    public DashboardFacade(Customer customer,
                           TransactionHandler transactionChain,
                           SupportHandler supportChain,
                           AccessControlManager accessControl,
                           AuthenticationService authService,
                           int txnThreadPoolSize) {
        this.customer = customer;
        this.transactionChain = transactionChain;
        this.supportChain = supportChain;
        this.accessControl = accessControl;
        this.authService = authService;
        this.transactionLogger = new TransactionLogger();
        this.recommendationEngine = new RecommendationEngine(new SpendingStrategy());
        this.txnExecutor = new TransactionExecutor(transactionChain, Math.max(1, txnThreadPoolSize));
        // Attach the logger to existing accounts (if any)
        addNotificationToAllAccounts(transactionLogger);
    }

    // Account Management (Factory Pattern)
    public Account createAccount(String type, String accountNumber) {
        if (!accessControl.isAllowed("createAccount")) {
            throw new SecurityException("No permission to create account. Required role: Teller or higher");
        }
        Account account = AccountFactory.createAccount(type, accountNumber);
        // Ensure account is associated with this customer
        account.setCustomer(customer);
        customer.addAccount(account);

        // Attach the transaction logger observer to the new account
        account.addObserver(transactionLogger);

        System.out.println("Account created and added to customer: " + account.getAccountNumber());
        account.notifyObservers(new AccountEvent("ACCOUNT_CREATED", account, null, "New account created for customer " + customer.getCustomerId()));
        return account;
    }

    // Transaction Processing (async)
    public Future<Void> processTransactionAsync(Account account, Account destinationAccount, Money amount, TransactionType type) {
        // Basic null checks
        if (account == null || amount == null || type == null) {
            throw new IllegalArgumentException("Account, amount and type are required");
        }

        // 1. large transaction permission
        if (amount.getAmount().compareTo(new BigDecimal("5000.00")) > 0) {
            if (!accessControl.isAllowed("largeTransaction")) {
                System.out.println("Transaction rejected: No permission for large transaction (" + amount + "). " +
                        "Current role: " + accessControl.getCurrentRole() +
                        " | Required: Manager or higher");
                CompletableFuture<Void> rejected = new CompletableFuture<>();
                rejected.completeExceptionally(new SecurityException("No permission for large transaction"));
                return rejected;
            } else {
                System.out.println("Large transaction approved by current role: " + accessControl.getCurrentRole());
            }
        }

        // Transfer requires destinationAccount
        if (type == TransactionType.TRANSFER && destinationAccount == null) {
            CompletableFuture<Void> rejected = new CompletableFuture<>();
            rejected.completeExceptionally(new IllegalArgumentException("TRANSFER requires a destination account"));
            return rejected;
        }

        // 2. state validation for source
        if (!account.getState().validateOperation(type)) {
            CompletableFuture<Void> rejected = new CompletableFuture<>();
            rejected.completeExceptionally(new IllegalStateException("Account state does not allow this operation: " + account.getStateDescription()));
            System.out.println("Transaction rejected: Account state does not allow this operation. Current state: " + account.getStateDescription());
            return rejected;
        }

        // 2b. for transfer, validate destination can accept deposits
        if (type == TransactionType.TRANSFER) {
            if (!destinationAccount.getState().validateOperation(TransactionType.DEPOSIT)) {
                CompletableFuture<Void> rejected = new CompletableFuture<>();
                rejected.completeExceptionally(new IllegalStateException("Destination account state does not allow deposit: " + destinationAccount.getStateDescription()));
                System.out.println("Transaction rejected: Destination account state does not allow deposit. Destination state: " + destinationAccount.getStateDescription());
                return rejected;
            }
        }

        // 3. apply account-specific strategy rules (strategy may augment amount e.g., add fees)
        Money processedAmount = account.getStrategy().applyRules(amount, type, account.getBalance());

        // 4. create transaction (use destination if transfer)
        Transaction transaction;
        if (type == TransactionType.TRANSFER) {
            transaction = new Transaction("TXN-" + System.currentTimeMillis(), account, destinationAccount, processedAmount, type);
        } else {
            transaction = new Transaction("TXN-" + System.currentTimeMillis(), account, processedAmount, type);
        }

        // Submit to executor for async processing
        return txnExecutor.submit(transaction);
    }

    // Synchronous wrapper that waits for completion (with timeout)
    public void processTransaction(Account account, Account destinationAccount, Money amount, TransactionType type) throws Exception {
        Future<Void> f = processTransactionAsync(account, destinationAccount, amount, type);
        try {
            f.get(60, TimeUnit.SECONDS);
        } catch (ExecutionException ee) {
            // unwrap and rethrow the cause
            Throwable cause = ee.getCause();
            if (cause instanceof Exception) throw (Exception) cause;
            else throw ee;
        } catch (TimeoutException te) {
            throw new RuntimeException("Transaction timed out", te);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw ie;
        }
    }

    // Convenience overload for non-transfer operations
    public Future<Void> processTransactionAsync(Account account, Money amount, TransactionType type) {
        return processTransactionAsync(account, null, amount, type);
    }

    public void processTransaction(Account account, Money amount, TransactionType type) throws Exception {
        processTransaction(account, null, amount, type);
    }

    // Recurring Transaction (non-transfer)
    public void scheduleRecurringTransaction(Account account, Money amount, TransactionType type, Schedule schedule) {
        scheduleRecurringTransaction(account, null, amount, type, schedule);
    }

    // Recurring Transaction (transfer-supported) — stores job and provides callback that reuses facade processing
    public void scheduleRecurringTransaction(Account account, Account destinationAccount, Money amount, TransactionType type, Schedule schedule) {
        if (!accessControl.isAllowed("normalTransaction")) {
            throw new SecurityException("No permission for recurring transaction");
        }

        if (type == TransactionType.TRANSFER && destinationAccount == null) {
            throw new IllegalArgumentException("TRANSFER requires a destination account for recurring transactions");
        }

        if (!account.getState().validateOperation(type)) {
            throw new IllegalStateException("State not allow recurring operation for source account");
        }

        if (type == TransactionType.TRANSFER) {
            if (!destinationAccount.getState().validateOperation(TransactionType.DEPOSIT)) {
                throw new IllegalStateException("Destination account state does not allow deposit for recurring transfer");
            }
        }

        RecurringTransaction recurring;
        String id = "REC-" + System.currentTimeMillis();
        if (type == TransactionType.TRANSFER) {
            recurring = new RecurringTransaction(
                    id,
                    account,
                    destinationAccount,
                    amount,
                    type,
                    schedule,
                    (Transaction txn) -> {
                        try {
                            // reuse facade's synchronous processing to preserve validations; handle exceptions locally
                            processTransaction(txn.getAccount(), txn.getDestinationAccount(), txn.getAmount(), txn.getType());
                        } catch (Exception e) {
                            System.out.println("[RECURRENCE] Error executing recurring transfer " + txn.getTransactionId() + ": " + e.getMessage());
                        }
                    }
            );
        } else {
            recurring = new RecurringTransaction(
                    id,
                    account,
                    amount,
                    type,
                    schedule,
                    (Transaction txn) -> {
                        try {
                            processTransaction(txn.getAccount(), txn.getAmount(), txn.getType());
                        } catch (Exception e) {
                            System.out.println("[RECURRENCE] Error executing recurring transaction " + txn.getTransactionId() + ": " + e.getMessage());
                        }
                    }
            );
        }

        recurringJobs.put(recurring.getTransactionId(), recurring);
        System.out.println("[RECURRENCE] Scheduled recurring transaction: " + recurring.getTransactionId());
    }

    /**
     * Cancel a recurring job by id. Returns true if cancelled, false if not found.
     */
    public boolean cancelRecurring(String recurringId) {
        RecurringTransaction job = recurringJobs.remove(recurringId);
        if (job != null) {
            job.cancel();
            System.out.println("[RECURRENCE] Cancelled: " + recurringId);
            return true;
        }
        return false;
    }

    /**
     * List current recurring job IDs.
     */
    public Collection<String> listRecurringJobs() {
        return recurringJobs.keySet();
    }

    // Reporting
    public void generateCustomerReport(boolean decorated) {
        if (!accessControl.isAllowed("viewReports")) {
            throw new SecurityException("No permission to view reports");
        }
        ReportComponent report = customer.generatePortfolioReport();
        if (decorated) {
            report = new com.bank.reports.ExportDecorator(
                    new com.bank.reports.SignatureDecorator(
                            new com.bank.reports.WatermarkDecorator(report)
                    )
            );
        }
        System.out.println("\nCustomer Report:");
        System.out.println(report.generate());
    }

    // Support
    public void raiseSupportTicket(String description) {
        Ticket ticket = new Ticket("TKT-" + System.currentTimeMillis(), customer.getCustomerId(), description);
        supportChain.handleTicket(ticket);
    }

    // Notification
    public void addNotificationToAllAccounts(Observer observer) {
        for (Account account : customer.getAccounts()) {
            account.addObserver(observer);
        }
    }

    // Get Transaction History
    public void printTransactionHistory() {
        if (!accessControl.isAllowed("viewReports")) {
            throw new SecurityException("No permission to view transaction history");
        }
        System.out.println("\nTransaction History:");
        for (com.bank.utils.AccountEvent event : transactionLogger.getHistory()) {
            System.out.println(event.getEventType() + " on " + (event.getAccount() != null ? event.getAccount().getAccountNumber() : "N/A") + ": " + event.getDetails());
        }
    }

    // Recommendation
    public void getRecommendations() {
        System.out.println("\nRecommendations:");
        System.out.println(recommendationEngine.generate(customer));
    }

    public Customer getCustomer() {
        return customer;
    }

    public AccessControlManager getAccessControl() {
        return accessControl;
    }

    /**
     * Shutdown the transaction executor gracefully.
     */
    public void shutdownExecutor() throws InterruptedException {
        txnExecutor.shutdownGracefully(10, TimeUnit.SECONDS);
    }

    // Freeze Account (requires Manager+)
    public void freezeAccount(Account account) {
        if (!accessControl.isAllowed("freezeAccount")) {
            System.out.println("Operation rejected: No permission to freeze account (required: Manager or higher). Current role: " + accessControl.getCurrentRole());
            return;
        }
        account.freeze();
        System.out.println("Account frozen successfully by " + accessControl.getCurrentRole());
    }

    // Suspend Account (requires Manager+)
    public void suspendAccount(Account account) {
        if (!accessControl.isAllowed("suspendAccount")) {
            System.out.println("Operation rejected: No permission to suspend account (required: Manager or higher). Current role: " + accessControl.getCurrentRole());
            return;
        }
        account.suspend();
        System.out.println("Account suspended successfully by " + accessControl.getCurrentRole());
    }

    // Close Account (Admin only)
    public void closeAccount(Account account) {
        if (!accessControl.isAllowed("closeAccount")) {
            System.out.println("Operation rejected: No permission to close account (required: Admin). Current role: " + accessControl.getCurrentRole());
            return;
        }
        account.close();
        System.out.println("Account closed successfully by " + accessControl.getCurrentRole());
    }
}