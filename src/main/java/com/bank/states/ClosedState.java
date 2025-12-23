package com.bank.states;

import com.bank.core.Account;
import com.bank.utils.Money;
import com.bank.utils.TransactionType;
import com.bank.utils.AccountEvent;
import com.bank.utils.PrintUtil;

public class ClosedState implements AccountState {

    @Override
    public void deposit(Account account, Money amount) {
        throw new IllegalStateException("Account is closed");
    }

    @Override
    public void withdraw(Account account, Money amount) {
        throw new IllegalStateException("Account is closed");
    }

    @Override
    public void close(Account account) {
        PrintUtil.println("Account already closed.");
    }

    @Override
    public void freeze(Account account) {
        throw new IllegalStateException("Cannot freeze a closed account");
    }

    @Override
    public void suspend(Account account) {
        throw new IllegalStateException("Cannot suspend a closed account");
    }

    @Override
    public void activate(Account account) {
        throw new IllegalStateException("Closed accounts cannot be reactivated");
    }

    @Override
    public String getStateDescription() {
        return "Closed - Permanent";
    }

    @Override
    public boolean validateOperation(TransactionType type) {
        return false;
    }
}
