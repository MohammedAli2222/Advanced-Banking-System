package com.bank.services;

import com.bank.core.*;
import com.bank.utils.Money;
import com.bank.utils.Currency;

import java.math.BigDecimal;

public class AccountFactory {

    public static Account createAccount(String type, String accountNumber, Money initialBalance) {
        switch (type.toUpperCase()) {
            case "SAVINGS":
                return new SavingsAccount(accountNumber, initialBalance);
            case "CHECKING":
                return new CheckingAccount(accountNumber, initialBalance);
            case "INVESTMENT":
                return new InvestmentAccount(accountNumber, initialBalance);
            case "LOAN":
                return new LoanAccount(accountNumber, initialBalance);
            default:
                throw new IllegalArgumentException("Unknown account type: " + type);
        }
    }

    public static Account createAccount(String type, String accountNumber) {
        return createAccount(type, accountNumber, new Money(BigDecimal.ZERO, Currency.USD));
    }
}