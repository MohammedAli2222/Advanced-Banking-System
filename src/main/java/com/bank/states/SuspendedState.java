package com.bank.states;

import com.bank.core.Account;
import com.bank.utils.Money;
import com.bank.utils.TransactionType;

public class SuspendedState implements AccountState {

    @Override
    public void deposit(Account account, Money amount) {
        System.out.println("Deposit not allowed in Suspended state.");
        throw new IllegalStateException("Account is suspended");
    }

    @Override
    public void withdraw(Account account, Money amount) {
        System.out.println("Withdrawal not allowed in Suspended state.");
        throw new IllegalStateException("Account is suspended");
    }

    @Override
    public void close(Account account) {
        account.setState(new ClosedState());
        System.out.println("Account closed from Suspended state.");
    }

    @Override
    public void freeze(Account account) {
        account.setState(new FrozenState());
        System.out.println("Account frozen from Suspended state.");
    }

    @Override
    public void suspend(Account account) {
        System.out.println("Account is already suspended.");
    }

    @Override
    public void activate(Account account) {
        account.setState(new ActiveState());
        System.out.println("Account activated from Suspended state.");
    }

    @Override
    public String getStateDescription() {
        return "Suspended - All operations blocked except admin actions";
    }

    @Override
    public boolean validateOperation(TransactionType type) {
        return false;  // مفيش عمليات
    }
}