package com.bank.core;

import com.bank.strategies.CheckingStrategy;
import com.bank.utils.Money;
import com.bank.utils.Currency;

import java.math.BigDecimal;

public class CheckingAccount extends Account {
    private Money overdraftLimit;

    public CheckingAccount(String accountNumber, Money initialBalance, Money overdraftLimit) {
        super(accountNumber, initialBalance, new CheckingStrategy());
        this.overdraftLimit = overdraftLimit;
    }

    public boolean isOverdraftAllowed(Money amount) {
        return getBalance().getAmount().add(overdraftLimit.getAmount()).compareTo(amount.getAmount()) >= 0;
    }

    @Override
    public Money getOverdraftLimitInternal() {
        return this.overdraftLimit; // الحقل الذي أضفناه اليوم في CheckingAccount
    }
}