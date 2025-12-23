package com.bank.core;

import com.bank.strategies.LoanStrategy;
import com.bank.utils.Money;
import com.bank.utils.Currency;

import java.math.BigDecimal;

public class LoanAccount extends Account {
    private Money loanAmount;        // مبلغ القرض [cite: 71]
    private double interestRate;     // نسبة فائدة القرض [cite: 71]
    // private PaymentSchedule paymentSchedule; // تحتاج لتعريف هذا الكلاس لاحقاً [cite: 71]

    public LoanAccount(String accountNumber, Money initialDebt, double interestRate) {
        super(accountNumber, initialDebt, new LoanStrategy());
        this.loanAmount = initialDebt;
        this.interestRate = interestRate;
    }

    public void applyInterest() {
        // تطبيق الفائدة على القرض حسب الرسم [cite: 73]
    }

    @Override
    public double getInterestRateInternal() {
        return this.interestRate; // نسبة فائدة القرض
    }

    // القروض عادة لا تملك Overdraft لأنها أصلاً مديونية
    @Override
    public Money getOverdraftLimitInternal() {
        return new Money(java.math.BigDecimal.ZERO, getBalance().getCurrency());
    }
}