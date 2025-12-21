package com.bank;

import com.bank.core.*;
import com.bank.utils.Currency;
import com.bank.utils.Money;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class StrategyPatternTest {

    @Test
    void testSavingStrategyInterest() {
        Account savings = new SavingsAccount("SAV-001");
        savings.deposit(new Money(new BigDecimal("10000"), Currency.USD));
        Money interest = savings.calculateInterest();
        assertEquals(new BigDecimal("400.00"), interest.getAmount()); // 4%
    }

    @Test
    void testInvestmentHighInterest() {
        Account investment = new InvestmentAccount("INV-001");
        investment.deposit(new Money(new BigDecimal("10000"), Currency.USD));
        Money interest = investment.calculateInterest();
        assertEquals(new BigDecimal("800.00"), interest.getAmount()); // 8%
    }

    @Test
    void testLoanInterestOnNegativeBalance() {
        Account loan = new LoanAccount("LOAN-001");
        // استخدم applyBalanceChange لتعريف رصيد سلبي (دين) بدلاً من setBalance
        loan.applyBalanceChange(new Money(new BigDecimal("-10000"), Currency.USD));
        Money interest = loan.calculateInterest();
        assertEquals(new BigDecimal("600.00"), interest.getAmount()); // 6% على الدين
    }
}