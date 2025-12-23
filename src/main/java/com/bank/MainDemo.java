package com.bank;

import com.bank.core.*;
import com.bank.customers.*;
import com.bank.security.*;
import com.bank.services.DashboardFacade;
import com.bank.transactions.*;
import com.bank.utils.*;
import com.bank.notifications.TransactionLogger;

import java.math.BigDecimal;

public class MainDemo {

    public static void main(String[] args) {
        try {
            printHeader("بدء تشغيل محاكاة النظام البنكي المتقدم v2.3 (Roles, Protection & Logs)");

            // 1. إعداد المستخدم
            ContactInfo contact = new ContactInfo("ahmed@example.com", "0555", "الرياض");
            Customer customer = new Customer("C-001", "أحمد المحارب", contact);

            // 2. Logger مركزي لجميع الحسابات
            TransactionLogger logger = new TransactionLogger();

            // 3. إعداد سلسلة المعاملات (Chain of Responsibility)
            TransactionHandler chain = new FinalExecutor();

            // 4. إعداد التحكم بالوصول باستخدام Admin (يمكن تغييره لاحقًا لأي دور)
            AccessControlManager auth = new AccessControlManager(new AdminState());

            // 5. إنشاء Facade
            DashboardFacade bankFacade = new DashboardFacade(customer, chain, null, auth, new AuthenticationService(), 2);

            // 6. إنشاء الحسابات
            Account checking = (Account) bankFacade.createAccount("CHECKING", "CH-101", true);
            Account savings = (Account) bankFacade.createAccount("SAVINGS", "SA-202", true);

            // 7. إضافة الـ Logger كمراقب لجميع الحسابات
            checking.addObserver(logger);
            savings.addObserver(logger);

            // ---------------- SCENARIO 1: Deposit ----------------
            printScenario("1. إيداع مبلغ 2000$ في حساب التوفير");
            Money depositAmount = new Money(new BigDecimal("2000"), Currency.USD);
            bankFacade.processTransaction(savings, null, depositAmount, TransactionType.DEPOSIT);
            System.out.println("✅ الرصيد الحالي لحساب التوفير: " + savings.getBalance());

            // ---------------- SCENARIO 2: Large Transfer with Protection ----------------
            printScenario("2. محاولة إجراء تحويل كبير مع حماية السحب المكشوف");
            // إضافة حماية السحب المكشوف
            AccountComponent protectedChecking = new OverdraftProtectionDecorator(
                    checking,
                    new Money(new BigDecimal("5000"), Currency.USD) // الحد المسموح
            );

            Money largeAmount = new Money(new BigDecimal("4000"), Currency.USD);
            bankFacade.processTransaction(protectedChecking, savings, largeAmount, TransactionType.TRANSFER);
            System.out.println("✅ الرصيد بعد التحويل الكبير: " + protectedChecking.getBalance());

            // ---------------- SCENARIO 3: Frozen Account ----------------
            printScenario("3. محاولة سحب من حساب مجمد");
            savings.freeze();
            try {
                bankFacade.processTransaction(savings, null, new Money(new BigDecimal("100"), Currency.USD), TransactionType.WITHDRAWAL);
            } catch (Exception e) {
                System.out.println("🛑 فشل العملية كما هو متوقع: " + e.getMessage());
            }

            // ---------------- SCENARIO 4: Generate Report ----------------
            printScenario("4. توليد تقرير مالي رسمي مزخرف (Watermark + Signature)");
            bankFacade.generateCustomerReport(true);

            printHeader("نهاية المحاكاة: جميع الأنظمة تعمل بتناغم");

        } catch (Exception e) {
            System.err.println("💥 خطأ غير متوقع: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚀 " + title);
        System.out.println("=".repeat(60));
    }

    private static void printScenario(String desc) {
        System.out.println("\n🔹 [SCENARIO]: " + desc);
        System.out.println("-".repeat(45));
    }
}
