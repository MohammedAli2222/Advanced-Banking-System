package com.bank.strategies;

import com.bank.utils.Money;
import com.bank.utils.TransactionType;

import java.math.BigDecimal;

public class InvestmentStrategy implements AccountStrategy {
    private static final BigDecimal INTEREST_RATE = new BigDecimal("0.08"); // 8% (مخاطر عالية)

    @Override
    public Money calculateInterest(Money balance) {
        BigDecimal interest = balance.getAmount().multiply(INTEREST_RATE);
        return new Money(interest, balance.getCurrency());
    }

    @Override
    public Money applyFees(Money balance) {
        // رسوم إدارة 1%
        BigDecimal fee = balance.getAmount().multiply(new BigDecimal("0.01"));
        return new Money(fee, balance.getCurrency());
    }

    @Override
    public Money withdrawRules(Money amount, Money balance) {
        // رسوم سحب مبكر 2%
        BigDecimal earlyFee = amount.getAmount().multiply(new BigDecimal("0.02"));
        return new Money(earlyFee, amount.getCurrency());
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