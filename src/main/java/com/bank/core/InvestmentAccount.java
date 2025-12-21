package com.bank.core;

import com.bank.strategies.InvestmentStrategy;
import com.bank.utils.Money;
import com.bank.utils.Currency;

import java.math.BigDecimal;

public class InvestmentAccount extends Account {

    public InvestmentAccount(String accountNumber, Money initialBalance) {
        super(accountNumber, initialBalance, new InvestmentStrategy());
    }

    public InvestmentAccount(String accountNumber) {
        this(accountNumber, new Money(BigDecimal.ZERO, Currency.USD));
    }
}