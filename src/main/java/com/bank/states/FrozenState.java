package com.bank.states;

import com.bank.core.Account;
import com.bank.utils.Money;
import com.bank.utils.TransactionType;
import com.bank.utils.PrintUtil;

/**
 * Frozen state: يسمح بالإيداعات فقط، ويسبِّب رفض السحب.
 */
public class FrozenState implements AccountState {

    @Override
    public void deposit(Account account, Money amount) {
        account.applyBalanceChange(account.getBalance().add(amount));
        PrintUtil.println("Deposit successful in Frozen state: " + amount);
    }

    @Override
    public void withdraw(Account account, Money amount) {
        PrintUtil.println("Withdrawal not allowed in Frozen state.");
        throw new IllegalStateException("Account is frozen");
    }

    @Override
    public void close(Account account) {
        account.setState(new ClosedState());
        PrintUtil.println("Account closed from Frozen state.");
    }

    @Override
    public void freeze(Account account) {
        PrintUtil.println("Account is already frozen.");
    }

    @Override
    public void suspend(Account account) {
        account.setState(new SuspendedState());
        PrintUtil.println("Account suspended from Frozen state.");
    }

    @Override
    public void activate(Account account) {
        account.setState(new ActiveState());
        PrintUtil.println("Account activated from Frozen state.");
    }

    @Override
    public String getStateDescription() {
        return "Frozen - Deposits allowed, withdrawals blocked";
    }

    @Override
    public boolean validateOperation(TransactionType type) {
        return type == TransactionType.DEPOSIT;
    }
}