package com.bank.core;

import com.bank.strategies.LoanStrategy;
import com.bank.utils.Money;
import com.bank.utils.Currency;

import java.math.BigDecimal;

public class LoanAccount extends Account {

    public LoanAccount(String accountNumber, Money initialDebt) {
        // الرصيد سالب للدين
        super(accountNumber, initialDebt, new LoanStrategy());
    }

    public LoanAccount(String accountNumber) {
        this(accountNumber, new Money(BigDecimal.ZERO, Currency.USD));
    }
}