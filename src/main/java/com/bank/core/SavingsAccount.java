package com.bank.core;

import com.bank.strategies.SavingStrategy;
import com.bank.utils.Money;
import com.bank.utils.Currency;

import java.math.BigDecimal;

public class SavingsAccount extends Account {

    public SavingsAccount(String accountNumber, Money initialBalance) {
        super(accountNumber, initialBalance, new SavingStrategy());
    }

    public SavingsAccount(String accountNumber) {
        this(accountNumber, new Money(BigDecimal.ZERO, Currency.USD));
    }
}