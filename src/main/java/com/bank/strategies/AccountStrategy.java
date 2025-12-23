package com.bank.strategies;

import com.bank.utils.Money;
import com.bank.utils.TransactionType;

public interface AccountStrategy {
    // مضاف: معامل interestRate
    Money calculateInterest(Money balance, double interestRate);

    Money applyFees(Money balance);

    // مضاف: معامل overdraftLimit
    Money withdrawRules(Money amount, Money balance, Money overdraftLimit);

    Money depositRules(Money amount);

    // مضاف: المعاملات الجديدة للدالة العامة
    Money applyRules(Money amount, TransactionType type, Money balance, double interestRate, Money overdraftLimit);
}