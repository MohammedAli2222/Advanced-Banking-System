package com.bank.strategies;

import com.bank.utils.Money;
import com.bank.utils.TransactionType;
import java.math.BigDecimal;

public class LoanStrategy implements AccountStrategy {

    @Override
    public Money calculateInterest(Money balance, double interestRate) {
        // الفائدة على القرض (الدين)
        BigDecimal debt = balance.getAmount().abs();
        BigDecimal interest = debt.multiply(BigDecimal.valueOf(interestRate));
        return new Money(interest, balance.getCurrency());
    }

    @Override
    public Money applyFees(Money balance) {
        return new Money(BigDecimal.ZERO, balance.getCurrency());
    }

    @Override
    public Money withdrawRules(Money amount, Money balance, Money overdraftLimit) {
        // لا يسمح بالسحب من حساب القرض عادةً
        throw new UnsupportedOperationException("Cannot withdraw from a Loan account.");
    }

    @Override
    public Money depositRules(Money amount) { return amount; }

    @Override
    public Money applyRules(Money amount, TransactionType type, Money balance, double interestRate, Money overdraftLimit) {
        if (type == TransactionType.DEPOSIT) {
            return depositRules(amount);
        }
        return new Money(BigDecimal.ZERO, amount.getCurrency());
    }
}