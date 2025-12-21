package com.bank.customers;

import com.bank.core.Account;
import com.bank.utils.ContactInfo;
import com.bank.reports.ReportComponent;
import com.bank.reports.ReportComposite;
import com.bank.reports.ReportLeaf;
import com.bank.utils.Currency;
import com.bank.utils.Money;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String customerId;
    private String name;
    private ContactInfo contactInfo;
    private List<Account> accounts = new ArrayList<>();

    public Customer(String customerId, String name, ContactInfo contactInfo) {
        this.customerId = customerId;
        this.name = name;
        this.contactInfo = contactInfo;
    }

    // Getters
    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public ContactInfo getContactInfo() { return contactInfo; }
    public List<Account> getAccounts() { return accounts; }

    // إضافة/إزالة حساب (Composite behavior)
    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
    }

    // حساب إجمالي الرصيد لكل الحسابات
    public Money getTotalBalance() {
        Money total = new Money(java.math.BigDecimal.ZERO, Currency.USD);
        for (Account account : accounts) {
            total = total.add(account.getBalance());
        }
        return total;
    }

    // تقرير مركب لكل الحسابات (يستخدم Composite من reports)
    public ReportComponent generatePortfolioReport() {
        ReportComposite portfolioReport = new ReportComposite("Portfolio Report for Customer " + name);
        for (Account account : accounts) {
            portfolioReport.add(new ReportLeaf(account));
        }
        return portfolioReport;
    }

    @Override
    public String toString() {
        return "Customer[" + customerId + "] Name: " + name +
                " | Contact: " + contactInfo +
                " | Accounts: " + accounts.size() +
                " | Total Balance: " + getTotalBalance();
    }
}