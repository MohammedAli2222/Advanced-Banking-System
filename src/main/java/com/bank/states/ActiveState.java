package com.bank.states;

import com.bank.core.Account;
import com.bank.utils.Money;
import com.bank.utils.TransactionType;
import com.bank.utils.AccountEvent;
import com.bank.utils.PrintUtil;

public class ActiveState implements AccountState {

    @Override
    public void deposit(Account account, Money amount) {
        account.applyBalanceChange(amount, TransactionType.DEPOSIT);

        // إشعار المراقبين
        account.notifyObservers(new AccountEvent(
                account,
                TransactionType.DEPOSIT.name(),
                "Deposit processed via Strategy in Active state",
                amount
        ));

        PrintUtil.println("Deposit processed via Strategy in Active state: " + amount);
    }

    @Override
    public void withdraw(Account account, Money amount) {
        account.applyBalanceChange(amount, TransactionType.WITHDRAWAL);

        account.notifyObservers(new AccountEvent(
                account,
                TransactionType.WITHDRAWAL.name(),
                "Withdrawal processed via Strategy in Active state",
                amount
        ));

        PrintUtil.println("Withdrawal processed via Strategy in Active state: " + amount);
    }

    @Override
    public void close(Account account) {
        if (account.getBalance().getAmount().doubleValue() < 0) {
            throw new IllegalStateException("Cannot close account with negative balance (Debt).");
        }
        account.setState(new ClosedState());

        account.notifyObservers(new AccountEvent(
                account,
                "STATE_CHANGE",
                "Account closed",
                null
        ));

        PrintUtil.println("Account moved to Closed state.");
    }

    @Override
    public void freeze(Account account) {
        account.setState(new FrozenState());

        account.notifyObservers(new AccountEvent(
                account,
                "STATE_CHANGE",
                "Account frozen",
                null
        ));

        PrintUtil.println("Account is now Frozen.");
    }

    @Override
    public void suspend(Account account) {
        account.setState(new SuspendedState());

        account.notifyObservers(new AccountEvent(
                account,
                "STATE_CHANGE",
                "Account suspended",
                null
        ));

        PrintUtil.println("Account is now Suspended.");
    }

    @Override
    public void activate(Account account) {
        PrintUtil.println("Account is already Active.");
    }

    @Override
    public String getStateDescription() {
        return "Active - Full operations allowed";
    }

    @Override
    public boolean validateOperation(TransactionType type) {
        return true;
    }
}
