package com.bank;

import com.bank.core.SavingsAccount;
import com.bank.customers.Customer;
import com.bank.reports.*;
import com.bank.utils.ContactInfo;
import com.bank.utils.Currency;
import com.bank.utils.Money;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ReportCompositeDecoratorTest {

    @Test
    void testDecoratedReportContainsWatermarkAndSignature() {
        Customer customer = new Customer("C001", "Ali", new ContactInfo("a@b.com", "123", "Riyadh"));
        SavingsAccount acc = new SavingsAccount("ACC-001");
        acc.deposit(new Money(new BigDecimal("5000"), Currency.USD));
        customer.addAccount(acc);

        ReportComponent report = customer.generatePortfolioReport();
        report = new WatermarkDecorator(new SignatureDecorator(new ExportDecorator(report)));

        String generated = report.generate();

        assertTrue(generated.contains("[WATERMARK: CONFIDENTIAL]"));
        assertTrue(generated.contains("Signed by: Bank Manager"));
        assertTrue(generated.contains("[EXPORTED TO PDF]"));
        assertTrue(generated.contains("ACC-001"));
    }
}