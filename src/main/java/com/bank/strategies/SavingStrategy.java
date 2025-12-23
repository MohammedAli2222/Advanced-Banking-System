package com.bank.strategies;

import com.bank.utils.Money;
import com.bank.utils.TransactionType;
import java.math.BigDecimal;

public class SavingStrategy implements AccountStrategy {

    @Override
    public Money calculateInterest(Money balance, double interestRate) {
        // استخدام المعامل الديناميكي interestRate بدلاً من القيمة الثابتة
        BigDecimal interestAmount = balance.getAmount().multiply(BigDecimal.valueOf(interestRate));
        return new Money(interestAmount, balance.getCurrency());
    }

    @Override
    public Money applyFees(Money balance) {
        if (balance.getAmount().compareTo(new BigDecimal("1000.00")) < 0) {
            return new Money(new BigDecimal("10.00"), balance.getCurrency());
        }
        return new Money(BigDecimal.ZERO, balance.getCurrency());
    }

    @Override
    public Money withdrawRules(Money amount, Money balance, Money overdraftLimit) {
        if (amount.getAmount().compareTo(balance.getAmount()) > 0) {
            throw new IllegalArgumentException("Insufficient funds in Savings Account!");
        }
        return new Money(BigDecimal.ZERO, amount.getCurrency());
    }

    @Override
    public Money depositRules(Money amount) { return amount; }

    @Override
    public Money applyRules(Money amount, TransactionType type, Money balance, double interestRate, Money overdraftLimit) {
        if (type == TransactionType.WITHDRAWAL) {
            return withdrawRules(amount, balance, overdraftLimit);
        }
        return new Money(BigDecimal.ZERO, amount.getCurrency());
    }
}