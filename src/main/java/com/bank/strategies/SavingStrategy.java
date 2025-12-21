package com.bank.strategies;

import com.bank.utils.Money;
import com.bank.utils.Currency;
import com.bank.utils.TransactionType;

import java.math.BigDecimal;

public class SavingStrategy implements AccountStrategy {
    private static final BigDecimal INTEREST_RATE = new BigDecimal("0.04"); // 4% سنوي (مثال)
    private static final BigDecimal MIN_BALANCE_FEE = new BigDecimal("10.00"); // رسوم إذا الرصيد أقل من حد

    @Override
    public Money calculateInterest(Money balance) {
        BigDecimal interestAmount = balance.getAmount().multiply(INTEREST_RATE);
        return new Money(interestAmount, balance.getCurrency());
    }

    @Override
    public Money applyFees(Money balance) {
        // مثال: رسوم إذا الرصيد أقل من 1000
        if (balance.getAmount().compareTo(new BigDecimal("1000.00")) < 0) {
            return new Money(MIN_BALANCE_FEE, balance.getCurrency());
        }
        return new Money(BigDecimal.ZERO, balance.getCurrency());
    }

    @Override
    public Money withdrawRules(Money amount, Money balance) {
        // مثال: لا رسوم إضافية للتوفير
        return new Money(BigDecimal.ZERO, amount.getCurrency());
    }

    @Override
    public Money depositRules(Money amount) {
        // مثال: bonus 1% على الإيداعات الكبيرة
        if (amount.getAmount().compareTo(new BigDecimal("5000.00")) > 0) {
            BigDecimal bonus = amount.getAmount().multiply(new BigDecimal("0.01"));
            return amount.add(new Money(bonus, amount.getCurrency()));
        }
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