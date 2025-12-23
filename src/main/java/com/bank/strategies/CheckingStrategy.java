package com.bank.strategies;

import com.bank.utils.Money;
import com.bank.utils.TransactionType;
import java.math.BigDecimal;

public class CheckingStrategy implements AccountStrategy {

    @Override
    public Money applyRules(Money amount, TransactionType type, Money balance, double interestRate, Money overdraftLimit) {
        if (type == TransactionType.WITHDRAWAL) {
            return withdrawRules(amount, balance, overdraftLimit);
        }
        // تصحيح: إرجاع المبلغ الممرر في حالة الإيداع لضمان عدم تصفيره
        if (type == TransactionType.DEPOSIT) {
            return amount;
        }
        return amount;
    }

    @Override
    public Money withdrawRules(Money amount, Money balance, Money overdraftLimit) {
        BigDecimal currentBal = balance.getAmount();
        BigDecimal withdrawAmt = amount.getAmount();
        // الحد المسموح به هو الرصيد + حد السحب المكشوف (Overdraft)
        BigDecimal limit = overdraftLimit.getAmount();

        if (currentBal.add(limit).compareTo(withdrawAmt) < 0) {
            throw new IllegalArgumentException("Overdraft limit exceeded! Max allowed with protection: " + limit);
        }
        return amount;
    }

    @Override
    public Money calculateInterest(Money balance, double interestRate) { return new Money(BigDecimal.ZERO, balance.getCurrency()); }
    @Override
    public Money applyFees(Money balance) { return new Money(BigDecimal.ZERO, balance.getCurrency()); }
    @Override
    public Money depositRules(Money amount) { return amount; }
}