package com.bank.strategies;

import com.bank.utils.Money;
import com.bank.utils.TransactionType;

import java.math.BigDecimal;

public class LoanStrategy implements AccountStrategy {
    private static final BigDecimal LOAN_INTEREST_RATE = new BigDecimal("0.06"); // 6% على الدين

    @Override
    public Money calculateInterest(Money balance) {
        BigDecimal debt = balance.getAmount().abs();
        BigDecimal interest = debt.multiply(LOAN_INTEREST_RATE);
        return new Money(interest, balance.getCurrency());
    }

    @Override
    public Money applyFees(Money balance) {
        return new Money(BigDecimal.ZERO, balance.getCurrency());
    }

    @Override
    public Money withdrawRules(Money amount, Money balance) {
        return amount;
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