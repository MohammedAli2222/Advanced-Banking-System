package com.bank.services;

import com.bank.core.*;
import com.bank.notifications.Observer;
import com.bank.notifications.TransactionLogger;
import com.bank.recommendations.RecommendationEngine;
import com.bank.recommendations.SpendingStrategy;
import com.bank.reports.ReportComponent;
import com.bank.security.AccessControlManager;
import com.bank.security.AuthenticationService;
import com.bank.security.Operation;
import com.bank.support.SupportHandler;
import com.bank.support.Ticket;
import com.bank.transactions.*;
import com.bank.utils.Money;
import com.bank.utils.Schedule;
import com.bank.utils.TransactionType;
import com.bank.customers.Customer;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.*;

public class DashboardFacade {

    private final Customer customer;
    private final TransactionHandler transactionChain;
    private final SupportHandler supportChain;
    private final TransactionLogger transactionLogger;
    private final RecommendationEngine recommendationEngine;
    private final AccessControlManager accessControl;
    private final AuthenticationService authService;
    private final TransactionExecutor txnExecutor;

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

        addNotificationToAllAccounts(transactionLogger);
    }

    // ========================= ACCOUNTS =========================

    public AccountComponent createAccount(String type, String id, boolean addToCustomer) {
        if (!accessControl.isAllowed(Operation.CREATE_ACCOUNT)) {
            throw new SecurityException("No permission");
        }

        AccountComponent account = AccountFactory.createAccount(type, id);

        if (account instanceof Account) {
            ((Account) account).setCustomer(customer);
        }

        if (addToCustomer) {
            customer.addAccount(account);
        }

        account.addObserver(transactionLogger);
        return account;
    }

    // ========================= TRANSACTIONS =========================

    public Future<Void> processTransactionAsync(
            AccountComponent source,
            AccountComponent destination,
            Money amount,
            TransactionType type) {

        validateTransaction(source, destination, amount, type);

        Transaction transaction;
        if (destination != null) {
            transaction = new Transaction(
                    "TXN",
                    source,
                    destination,
                    amount,
                    type
            );
        } else {
            transaction = new Transaction(
                    "TXN",
                    source,
                    amount,
                    type
            );
        }

        return txnExecutor.submit(transaction);
    }

    public void processTransaction(
            AccountComponent source,
            AccountComponent destination,
            Money amount,
            TransactionType type) throws Exception {

        processTransactionAsync(source, destination, amount, type)
                .get(60, TimeUnit.SECONDS);
    }

    // ========================= RECURRING =========================

    public void scheduleRecurringTransaction(
            AccountComponent source,
            AccountComponent destination,
            Money amount,
            TransactionType type,
            Schedule schedule) {

        if (!accessControl.isAllowed(Operation.NORMAL_TRANSACTION)) {
            throw new SecurityException("No permission");
        }

        String id = "REC-" + System.currentTimeMillis();

        RecurringTransaction recurring = new RecurringTransaction(
                id, source, destination, amount, type, schedule,
                txn -> {
                    try {
                        processTransaction(
                                txn.getAccount(),
                                txn.getDestinationAccount(),
                                txn.getAmount(),
                                txn.getType()
                        );
                    } catch (Exception e) {
                        System.err.println("Recurring Task Failed: " + e.getMessage());
                    }
                }
        );

        recurringJobs.put(id, recurring);
    }

    // ========================= REPORTS =========================

    public void generateCustomerReport(boolean decorated) {
        if (!accessControl.isAllowed(Operation.VIEW_REPORTS)) {
            throw new SecurityException("Access Denied");
        }

        ReportComponent report = customer.generatePortfolioReport();

        if (decorated) {
            report = new com.bank.reports.ExportDecorator(
                    new com.bank.reports.SignatureDecorator(
                            new com.bank.reports.WatermarkDecorator(report)
                    )
            );
        }

        System.out.println("\n--- FINAL CUSTOMER REPORT ---\n" + report.generate());
    }

    // ========================= SUPPORT =========================

    public void raiseSupportTicket(String description) {
        Ticket ticket = new Ticket(
                "TKT-" + System.currentTimeMillis(),
                customer.getCustomerId(),
                description
        );
        supportChain.handleTicket(ticket);
    }

    // ========================= ACCOUNT CONTROL =========================

    public void freezeAccount(AccountComponent component) {
        if (accessControl.isAllowed(Operation.FREEZE_ACCOUNT) && component instanceof Account) {
            ((Account) component).freeze();
        }
    }

    public void closeAccount(AccountComponent component) {
        if (accessControl.isAllowed(Operation.CLOSE_ACCOUNT) && component instanceof Account) {
            ((Account) component).close();
        }
    }

    // ========================= UTILITIES =========================

    public void addNotificationToAllAccounts(Observer observer) {
        for (AccountComponent component : customer.getAccounts()) {
            component.addObserver(observer);
        }
    }

    public void getRecommendations() {
        System.out.println(recommendationEngine.generate(customer));
    }

    public Customer getCustomer() {
        return customer;
    }

    public void shutdown() throws InterruptedException {
        txnExecutor.shutdownGracefully(5, TimeUnit.SECONDS);
    }

    // ========================= VALIDATION =========================
    private void validateTransaction(AccountComponent source,
                                     AccountComponent destination,
                                     Money amount,
                                     TransactionType type) {

        if (amount == null || amount.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }

        // صلاحيات المبالغ الكبيرة
        if (amount.getAmount().compareTo(new BigDecimal("5000")) > 0) {
            if (!accessControl.isAllowed(Operation.LARGE_TRANSACTION)) {
                throw new SecurityException("Insufficient permissions for large transaction");
            }
        }

        // التحقق من التحويل
        if (type == TransactionType.TRANSFER && destination == null) {
            throw new IllegalArgumentException("Transfer requires destination account");
        }
    }

}
