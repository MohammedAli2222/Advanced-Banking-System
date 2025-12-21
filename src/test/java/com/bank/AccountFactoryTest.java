package com.bank;

import com.bank.core.*;
import com.bank.services.AccountFactory;
import com.bank.utils.Currency;
import com.bank.utils.Money;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class AccountFactoryTest {

    @Test
    void testCreateSavingsAccount() {
        Account account = AccountFactory.createAccount("SAVINGS", "SAV-001");
        assertInstanceOf(SavingsAccount.class, account);
        assertEquals("SAV-001", account.getAccountNumber());
    }

    @Test
    void testCreateCheckingAccount() {
        Account account = AccountFactory.createAccount("CHECKING", "CHK-001", new Money(new BigDecimal("1000"), Currency.USD));
        assertInstanceOf(CheckingAccount.class, account);
        assertEquals(new BigDecimal("1000"), account.getBalance().getAmount());
    }

    @Test
    void testCreateInvestmentAccount() {
        Account account = AccountFactory.createAccount("INVESTMENT", "INV-001");
        assertInstanceOf(InvestmentAccount.class, account);
    }

    @Test
    void testCreateLoanAccount() {
        Account account = AccountFactory.createAccount("LOAN", "LOAN-001");
        assertInstanceOf(LoanAccount.class, account);
    }

    @Test
    void testUnknownTypeThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                AccountFactory.createAccount("UNKNOWN", "XXX"));
    }
}