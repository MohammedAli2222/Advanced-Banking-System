package com.bank.states;

import com.bank.core.Account;
import com.bank.utils.Money;
import com.bank.utils.TransactionType;

public class ClosedState implements AccountState {

    @Override
    public void deposit(Account account, Money amount) {
        System.out.println("Cannot deposit into a closed account.");
        throw new IllegalStateException("Account is closed");
    }

    @Override
    public void withdraw(Account account, Money amount) {
        System.out.println("Cannot withdraw from a closed account.");
        throw new IllegalStateException("Account is closed");
    }

    @Override
    public void close(Account account) {
        System.out.println("Account is already closed.");
    }

    @Override
    public void freeze(Account account) {
        System.out.println("Cannot freeze a closed account.");
        throw new IllegalStateException("Account is closed");
    }

    @Override
    public void suspend(Account account) {
        System.out.println("Cannot suspend a closed account.");
        throw new IllegalStateException("Account is closed");
    }

    @Override
    public void activate(Account account) {
        System.out.println("Cannot activate a closed account.");
        throw new IllegalStateException("Account is closed");
    }

    @Override
    public String getStateDescription() {
        return "Closed - No operations allowed";
    }

    @Override
    public boolean validateOperation(TransactionType type) {
        return false;
    }
}