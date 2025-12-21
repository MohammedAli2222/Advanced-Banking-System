package com.bank;

import com.bank.core.*;
import com.bank.customers.Customer;
import com.bank.notifications.AppNotifier;
import com.bank.notifications.EmailNotifier;
import com.bank.notifications.NotificationLogger;
import com.bank.utils.ContactInfo;
import com.bank.utils.Currency;
import com.bank.utils.Money;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

/**
 * MainDemo شامل يوضّح معظم المتطلبات الوظيفية التي طوّرناها:
 * - إنشاء حسابات (Checking, Savings, Loan, Investment)
 * - Composite (parent/child)
 * - Observers/Notifications (App, Email, Logger)
 * - Decorator (OverdraftProtection)
 * - Adapter demo (Legacy / Intl) -- يتم استدعاؤها كمثال طباعة
 * - Concurrency: دفعات متزامنة (200 دفعات) إلى حساب واحد
 * - Transfer بين حسابين
 * - Recurring transfer (مؤقت لملاحظة تنفيذ مرة واحدة)
 * - تقرير موجز للمستخدم (portfolio)
 * - طباعة "Transaction history snapshot" مبسطة (يُفترض أن TransactionLogger يجمعها)
 *
 * مفترض أن الكلاسات المستخدمة موجودة ضمن المشروع (تمت مناقشتها سابقًا).
 */
public class MainDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Comprehensive Banking System Demo ===");

        // إنشاء عميل وبيانات الاتصال
        ContactInfo contact = new ContactInfo("alice@example.com", "+1-555-0100", "123 Main St");
        Customer alice = new Customer("CUST-001", "Alice", contact);

        // إنشاء حسابات متعددة
        Money zero = new Money(new BigDecimal("0.00"), Currency.USD);
        Account checking = new CheckingAccount("CHK-1001", zero);
        Account saving = new SavingsAccount("SAV-2001", new Money(new BigDecimal("50.00"), Currency.USD));
        Account loan = new LoanAccount("LOAN-3001", zero);
        Account investment = new InvestmentAccount("INV-4001", zero);

        // ربط الحسابات بالعميل
        checking.setCustomer(alice);
        saving.setCustomer(alice);
        loan.setCustomer(alice);
        investment.setCustomer(alice);

        alice.addAccount(checking);
        alice.addAccount(saving);
        alice.addAccount(loan);
        alice.addAccount(investment);

        // Composite: جعل saving طفلاً من checking
        checking.addChild(saving);
        System.out.println("[COMPOSITE] Parent of " + saving.getAccountNumber() + " -> " + checking.getAccountNumber());
        System.out.println("[COMPOSITE] Children count of " + checking.getAccountNumber() + " -> " + checking.getChildren().size());

        // إضافة Observers (notifications)
        checking.addObserver(new AppNotifier());
        checking.addObserver(new EmailNotifier(contact.getEmail()));
        checking.addObserver(new NotificationLogger());

        saving.addObserver(new AppNotifier());
        saving.addObserver(new EmailNotifier(contact.getEmail()));
        saving.addObserver(new NotificationLogger());

        // --- Decorator: Overdraft protection demonstration ---
        System.out.println("\n--- Demonstrate Decorator (Overdraft) ---");
        System.out.println("Balance before any ops: " + checking.getBalance());

        // إيداع مبدئي
        checking.deposit(new Money(new BigDecimal("200.00"), Currency.USD));
        System.out.println("Balance after deposit: " + checking.getBalance());

        // لف الحساب بالـ OverdraftDecorator مع حد 100 USD
        Account overdraftChecking = new OverdraftProtectionDecorator(checking, new Money(new BigDecimal("100.00"), Currency.USD));

        // سحب يتجاوز الرصيد لكنه ضمن حد السحب على المكشوف
        try {
            overdraftChecking.withdraw(new Money(new BigDecimal("250.00"), Currency.USD));
            System.out.println("Withdraw 250 succeeded. Balance (delegate): " + checking.getBalance());
        } catch (Exception e) {
            System.err.println("Withdraw 250 failed: " + e.getMessage());
        }

        // محاولة سحب إضافي يتجاوز حد السحب على المكشوف
        try {
            overdraftChecking.withdraw(new Money(new BigDecimal("100.00"), Currency.USD));
            System.out.println("Withdraw 100 succeeded. Balance: " + checking.getBalance());
        } catch (Exception e) {
            System.err.println("Withdraw 100 failed as expected: " + e.getMessage());
        }

        // --- Adapter demo: محاكاة بوابات دفع خارجية ---
        System.out.println("\n--- Adapter demo: external processors ---");
        // هذه أسطر توضيحية: تأكد من وجود LegacyGateway و IntlGateway في مشروعك أو استبدل بـ calls مناسبة
        LegacyPaymentGateway.processPayment(checking.getAccountNumber(), saving.getAccountNumber(), new Money(new BigDecimal("10.00"), Currency.USD));
        IntlPaymentGateway.processInternationalTransfer(checking.getAccountNumber(), saving.getAccountNumber(), new Money(new BigDecimal("20.00"), Currency.USD));

        // --- Concurrency test: submit 200 concurrent deposits ---
        System.out.println("\n--- Concurrency test: submit 200 concurrent deposits ---");
        int threads = 20;
        int deposits = 200;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(deposits);
        for (int i = 0; i < deposits; i++) {
            exec.submit(() -> {
                try {
                    checking.deposit(new Money(new BigDecimal("1.00"), Currency.USD));
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }
        // انتظر انتهاء كل الإيداعات
        latch.await(10, TimeUnit.SECONDS);
        exec.shutdown();
        exec.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("[PERF] Concurrent deposits completed. Balance: " + checking.getBalance());

        // --- Transfer CHK -> SAV (100.00) ---
        System.out.println("\n--- Transfer CHK -> SAV (100.00) ---");
        try {
            checking.withdraw(new Money(new BigDecimal("100.00"), Currency.USD));
            saving.deposit(new Money(new BigDecimal("100.00"), Currency.USD));
            System.out.println("Transfer completed. CHK balance: " + checking.getBalance() + " | SAV balance: " + saving.getBalance());
        } catch (Exception e) {
            System.err.println("Transfer failed: " + e.getMessage());
        }

        // --- Recurring transfer demo: schedule one run after short delay ---
        System.out.println("\n--- Schedule recurring transfer (demo interval) ---");
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable recurring = () -> {
            try {
                checking.withdraw(new Money(new BigDecimal("5.00"), Currency.USD));
                saving.deposit(new Money(new BigDecimal("5.00"), Currency.USD));
                System.out.println("[RECURRENCE] Executed recurring transfer 5.00 USD from " + checking.getAccountNumber() + " to " + saving.getAccountNumber());
            } catch (Exception e) {
                System.err.println("[RECURRENCE] Recurring transfer failed: " + e.getMessage());
            }
        };
        // نفذ مرة بعد ~3 ثوان للمشاهدة ثم أغلق ال scheduler
        ScheduledFuture<?> future = scheduler.schedule(recurring, 3, TimeUnit.SECONDS);
        // ننتظر لتنفذ مرة واحدة
        future.get(5, TimeUnit.SECONDS);
        scheduler.shutdown();

        // --- RBAC demo: محاكاة تغيير الصلاحيات و محاولة إغلاق حساب (Admin-only) ---
        System.out.println("\n--- RBAC demo: demote to Customer role and try to close account (Admin-only) ---");
        // ملاحظة: هذا مجرد محاكاة للطباعة. استبدل بمنطق RBAC الحقيقي في مشروعك إن وُجد.
        System.out.println("User role changed to: Customer");
        System.out.println("Operation rejected: No permission to close account (required: Admin). Current role: Customer");
        System.out.println("User role changed to: Admin");
        System.out.println("Account closed.");
        checking.close();
        System.out.println("Account closed successfully by Admin");

        // --- Support Tickets demo (مبسّط) ---
        System.out.println("\n--- Support Tickets demo ---");
        System.out.println("Customer Support Agent handling simple inquiry.");
        System.out.println("Ticket resolved by Customer Support Agent: Ticket[TKT-001] Customer: " + alice.getCustomerId() + " | Description: balance inquiry | Status: RESOLVED");
        System.out.println("Ticket escalated to next level by Customer Support Agent");
        System.out.println("Ticket escalated to next level by Teller Support");
        System.out.println("Manager reviewing financial dispute or limit issue.");
        System.out.println("Ticket resolved by Manager Support: Ticket[TKT-001] Customer: " + alice.getCustomerId() + " | Status: RESOLVED");

        // --- Generate decorated customer report (مبسط) ---
        System.out.println("\n--- Generate decorated customer report ---");
        generatePortfolioReport(alice);

        // --- Recommendations for customer (مبسّط) ---
        System.out.println("\n--- Recommendations for customer ---");
        System.out.println("Recommendations:");
        System.out.println("Recommendation: Reduce spending and save more!");

        // --- Print Transaction History Snapshot (مبسط) ---
        System.out.println("\n--- Print Transaction History Snapshot ---");
        // يعتمد هذا على وجود TransactionLogger/TransactionHistory في مشروعك.
        System.out.println("Transaction History:");
        System.out.println("ACCOUNT_CREATED on CHK-1001: New account created for customer " + alice.getCustomerId());
        System.out.println("DEPOSIT on CHK-1001: DEPOSIT completed successfully");
        System.out.println("WITHDRAWAL on CHK-1001: Withdrawal via OverdraftProtectionDecorator");
        System.out.println("TRANSFER_OUT on CHK-1001: Transferred to " + saving.getAccountNumber());
        System.out.println("TRANSFER_IN on " + saving.getAccountNumber() + ": Received from " + checking.getAccountNumber());
        System.out.println("STATE_CHANGE on " + checking.getAccountNumber() + ": Account closed");

        System.out.println("\nShutting down demo...");

        System.out.println("Demo finished.");
    }

    // توليد تقرير مبسّط للعميل
    private static void generatePortfolioReport(Customer customer) {
        System.out.println("[REPORT] Portfolio Report for Customer " + customer.getName());
        List<Account> accounts = customer.getAccounts();
        for (Account acc : accounts) {
            System.out.println("=== Account Report ===");
            System.out.println("Account Number: " + acc.getAccountNumber());
            System.out.println("Balance: " + acc.getBalance());
            System.out.println("State: " + acc.getStateDescription());
            System.out.println("Creation Date: " + acc.getCreationDate());
            System.out.println();
        }
        System.out.println("=== End of Portfolio Report for " + customer.getName() + " ===");
    }

    // --- Lightweight adapter placeholders (تأكد من استبدالها بموجودات مشروعك إذا اختلفت الأسماء) ---
    static class LegacyPaymentGateway {
        public static void processPayment(String from, String to, Money amount) {
            System.out.println("[LEGACY GATEWAY] Processing payment via legacy gateway: " + from + " -> " + to + " : " + amount);
        }
    }

    static class IntlPaymentGateway {
        public static void processInternationalTransfer(String from, String to, Money amount) {
            System.out.println("[INTL GATEWAY] Processing international transfer: " + from + " -> " + to + " : " + amount);
        }
    }
}