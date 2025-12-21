package com.bank.core;

import com.bank.strategies.CheckingStrategy;
import com.bank.utils.Money;
import com.bank.utils.Currency;

import java.math.BigDecimal;

public class CheckingAccount extends Account {

    public CheckingAccount(String accountNumber, Money initialBalance) {
        super(accountNumber, initialBalance, new CheckingStrategy());
    }

    public CheckingAccount(String accountNumber) {
        this(accountNumber, new Money(BigDecimal.ZERO, Currency.USD));
    }
}