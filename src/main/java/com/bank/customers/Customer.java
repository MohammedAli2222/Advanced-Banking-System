package com.bank.customers;

import com.bank.core.AccountComponent;
import com.bank.core.CompositeAccount;
import com.bank.core.Account;
import com.bank.reports.ReportComponent;
import com.bank.reports.ReportComposite;
import com.bank.reports.ReportLeaf;
import com.bank.utils.ContactInfo;
import com.bank.utils.Currency;
import com.bank.utils.Money;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Customer {

    private static final Logger logger = LoggerFactory.getLogger(Customer.class);

    private String customerId;
    private String name;
    private ContactInfo contactInfo;
    private CompositeAccount portfolio;
    private List<AccountComponent> accounts = new ArrayList<>();

    public Customer(String customerId, String name, ContactInfo contactInfo) {
        this.customerId = customerId;
        this.name = name;
        this.contactInfo = contactInfo;
        this.portfolio = new CompositeAccount("محفظة العميل: " + name);
        logger.info("Customer {} created with ID {}", name, customerId);
    }

    // إضافة حساب
    public void addAccount(AccountComponent account) {
        accounts.add(account);
        portfolio.add(account); // إضافة تلقائية للمحفظة (Composite)
        logger.info("Added account {} to customer {}", account instanceof Account ? ((Account) account).getAccountNumber() : "Composite", customerId);
    }

    // إزالة حساب
    public void removeAccount(AccountComponent account) {
        accounts.remove(account);
        portfolio.remove(account);
        logger.info("Removed account {} from customer {}", account instanceof Account ? ((Account) account).getAccountNumber() : "Composite", customerId);
    }

    // حساب إجمالي الرصيد لكل الحسابات
    public Money getTotalBalance() {
        Money total = portfolio.getBalance(); // الاستفادة من Composite مباشرة
        logger.info("Total balance for customer {} calculated: {}", customerId, total);
        return total;
    }

    // توليد تقرير المحفظة
    public ReportComponent generatePortfolioReport() {
        ReportComposite portfolioReport = new ReportComposite("Portfolio Report for Customer " + name);
        addComponentsToReport(this.accounts, portfolioReport);
        logger.info("Generated portfolio report for customer {}", customerId);
        return portfolioReport;
    }

    private void addComponentsToReport(List<AccountComponent> components, ReportComposite report) {
        for (AccountComponent component : components) {
            if (component instanceof Account) {
                report.add(new ReportLeaf((Account) component));
            } else if (component instanceof CompositeAccount) {
                CompositeAccount group = (CompositeAccount) component;
                addComponentsToReport(group.getChildren(), report);
            }
        }
    }


}
