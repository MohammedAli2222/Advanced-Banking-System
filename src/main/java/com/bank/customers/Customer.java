package com.bank.customers;

import com.bank.core.AccountComponent; // تغيير الاستيراد للواجهة الجديدة
import com.bank.core.CompositeAccount;
import com.bank.utils.ContactInfo;
import com.bank.reports.ReportComponent;
import com.bank.reports.ReportComposite;
import com.bank.reports.ReportLeaf;
import com.bank.utils.Currency;
import com.bank.utils.Money;
import com.bank.core.Account; // سنحتاجه للتقارير
import com.bank.core.CompositeAccount;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String customerId;
    private String name;
    private ContactInfo contactInfo;
    private CompositeAccount portfolio;
    // التعديل الأساسي: القائمة الآن تقبل أي شيء يطبق AccountComponent
    private List<AccountComponent> accounts = new ArrayList<>();

    public Customer(String customerId, String name, ContactInfo contactInfo) {
        this.customerId = customerId;
        this.name = name;
        this.contactInfo = contactInfo;
        this.portfolio = new CompositeAccount("محفظة العميل: " + name);
    }

    // Getters
    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public ContactInfo getContactInfo() { return contactInfo; }

    // إرجاع الواجهة العامة لضمان المرونة
    public List<AccountComponent> getAccounts() { return accounts; }

    // إضافة/إزالة مكون (سواء كان حساباً مفرداً أو مجموعة حسابات)
    public void addAccount(AccountComponent account) {
        accounts.add(account);
    }

    public void removeAccount(AccountComponent account) {
        accounts.remove(account);
    }

    /**
     * حساب إجمالي الرصيد لكل الحسابات.
     * هنا تظهر قوة الـ Composite:
     * إذا كان المكون حساباً عادياً، سيعيد رصيده.
     * إذا كان CompositeAccount، فإنه داخلياً سيجمع أرصدة أبنائه ويعيدها هنا.
     */
    public Money getTotalBalance() {
        Money total = new Money(java.math.BigDecimal.ZERO, Currency.USD);
        for (AccountComponent account : accounts) {
            total = total.add(account.getBalance());
        }
        return total;
    }

    /**
     * توليد تقرير المحفظة.
     * ملاحظة: قد تحتاج لتعديل ReportLeaf ليدعم AccountComponent بدلاً من Account
     */
    public ReportComponent generatePortfolioReport() {
        ReportComposite portfolioReport = new ReportComposite("Portfolio Report for Customer " + name);

        // نستخدم دالة مساعدة لضمان المرور على كل المستويات (الشجرة كاملة)
        addComponentsToReport(this.accounts, portfolioReport);

        return portfolioReport;
    }

    public CompositeAccount getPortfolio() {
        return this.portfolio;
    }

    private void addComponentsToReport(List<AccountComponent> components, ReportComposite report) {
        for (AccountComponent component : components) {
            if (component instanceof Account) {
                // إذا كان حساب فردي، أضفه كـ Leaf
                report.add(new ReportLeaf((Account) component));
            }
            else if (component instanceof CompositeAccount) {
                // إذا كانت مجموعة، نأخذ أبناءها ونضيفهم للتقرير أيضاً (Recursion)
                CompositeAccount group = (CompositeAccount) component;
                addComponentsToReport(group.getChildren(), report);
            }
        }
    }
    @Override
    public String toString() {
        return "Customer[" + customerId + "] Name: " + name +
                " | Contact: " + contactInfo +
                " | Components: " + accounts.size() +
                " | Total Portfolio Balance: " + getTotalBalance();
    }
}