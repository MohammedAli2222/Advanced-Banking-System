package com.bank.states;

import com.bank.core.Account;
import com.bank.utils.Money;
import com.bank.utils.TransactionType;
import com.bank.utils.AccountEvent;
import com.bank.utils.PrintUtil;

public class SuspendedState implements AccountState {

    @Override
    public void deposit(Account account, Money amount) {
        PrintUtil.println("❌ Rejected: Account is Suspended.");

        account.notifyObservers(new AccountEvent(
                account,
                TransactionType.DEPOSIT.name(),
                "Deposit rejected: Account is Suspended",
                amount
        ));

        throw new IllegalStateException("Account suspended");
    }

    @Override
    public void withdraw(Account account, Money amount) {
        PrintUtil.println("❌ Rejected: Account is Suspended.");

        account.notifyObservers(new AccountEvent(
                account,
                TransactionType.WITHDRAWAL.name(),
                "Withdrawal rejected: Account is Suspended",
                amount
        ));

        throw new IllegalStateException("Account suspended");
    }

    @Override
    public void close(Account account) {
        account.setState(new ClosedState());

        account.notifyObservers(new AccountEvent(
                account,
                "STATE_CHANGE",
                "Suspended account closed",
                null
        ));

        PrintUtil.println("Suspended account closed.");
    }

    @Override
    public void freeze(Account account) {
        account.setState(new FrozenState());

        account.notifyObservers(new AccountEvent(
                account,
                "STATE_CHANGE",
                "Suspended account frozen",
                null
        ));

        PrintUtil.println("Suspended account frozen.");
    }

    @Override
    public void suspend(Account account) {
        PrintUtil.println("Account is already suspended.");
    }

    @Override
    public void activate(Account account) {
        account.setState(new ActiveState());

        account.notifyObservers(new AccountEvent(
                account,
                "STATE_CHANGE",
                "Account reactivated from Suspended",
                null
        ));

        PrintUtil.println("✅ Suspension lifted: Account Active.");
    }

    @Override
    public String getStateDescription() {
        return "Suspended - All financial operations blocked";
    }

    @Override
    public boolean validateOperation(TransactionType type) {
        return false;
    }
}
