package com.bank.core;

import com.bank.strategies.SavingStrategy;
import com.bank.utils.Money;
import com.bank.utils.Currency;

import java.math.BigDecimal;

public class SavingsAccount extends Account {
    private double interestRate; // مضاف حسب الرسم [cite: 67]

    public SavingsAccount(String accountNumber, Money initialBalance, double interestRate) {
        super(accountNumber, initialBalance, new SavingStrategy());
        this.interestRate = interestRate;
    }

    // دالة حساب الفائدة الشهرية حسب الرسم [cite: 69]
    public Money calculateMonthlyInterest() {
        BigDecimal interest = getBalance().getAmount().multiply(BigDecimal.valueOf(interestRate / 12));
        return new Money(interest, getBalance().getCurrency());
    }

    @Override
    public double getInterestRateInternal() {
        return this.interestRate; // الحقل الذي أضفناه اليوم في SavingsAccount
    }

    @Override
    public void print() {
        System.out.println("🏦 [Savings Account] No: " + getAccountNumber() +
                " | Current Balance: " + getBalance());
    }


}