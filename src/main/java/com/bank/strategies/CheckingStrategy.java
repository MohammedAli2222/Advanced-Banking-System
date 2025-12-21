package com.bank.strategies;

import com.bank.utils.Money;
import com.bank.utils.TransactionType;

import java.math.BigDecimal;

public class CheckingStrategy implements AccountStrategy {
    private static final BigDecimal OVERDRAFT_FEE = new BigDecimal("35.00"); // رسوم overdraft

    @Override
    public Money calculateInterest(Money balance) {
        // عادة لا فوائد في الجاري
        return new Money(BigDecimal.ZERO, balance.getCurrency());
    }

    @Override
    public Money applyFees(Money balance) {
        // رسوم شهرية ثابتة مثلاً
        return new Money(new BigDecimal("5.00"), balance.getCurrency());
    }

    @Override
    public Money withdrawRules(Money amount, Money balance) {
        if (amount.getAmount().compareTo(balance.getAmount()) > 0) {
            return new Money(OVERDRAFT_FEE, amount.getCurrency());
        }
        return new Money(BigDecimal.ZERO, amount.getCurrency());
    }

    @Override
    public Money depositRules(Money amount) {
        return amount;
    }

    @Override
    public Money applyRules(Money amount, TransactionType type, Money balance) {
        // مثال: قواعد بسيطة, يمكن تخصيص حسب النوع
        if (type == TransactionType.DEPOSIT) {
            // bonus لإيداع كبير
            if (amount.getAmount().compareTo(new BigDecimal("5000.00")) > 0) {
                BigDecimal bonus = amount.getAmount().multiply(new BigDecimal("0.01"));
                return amount.add(new Money(bonus, amount.getCurrency()));
            }
        } else if (type == TransactionType.WITHDRAWAL) {
            // رسوم إضافية إذا رصيد منخفض
            if (balance.getAmount().compareTo(new BigDecimal("1000.00")) < 0) {
                BigDecimal fee = new BigDecimal("10.00");
                return amount.add(new Money(fee, amount.getCurrency()));
            }
        }
        return amount;
    }
}