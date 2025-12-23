package com.bank.states;

import com.bank.core.Account;
import com.bank.utils.Money;
import com.bank.utils.TransactionType;
import com.bank.utils.AccountEvent;
import com.bank.utils.PrintUtil;

public class FrozenState implements AccountState {

    @Override
    public void deposit(Account account, Money amount) {
        account.applyBalanceChange(amount, TransactionType.DEPOSIT);

        account.notifyObservers(new AccountEvent(
                account,
                TransactionType.DEPOSIT.name(),
                "Deposit processed in Frozen state",
                amount
        ));

        PrintUtil.println("Deposit processed in Frozen state: " + amount);
    }

    @Override
    public void withdraw(Account account, Money amount) {
        PrintUtil.println("🛡️ Security Alert: Withdrawal blocked! Account is Frozen.");

        account.notifyObservers(new AccountEvent(
                account,
                TransactionType.WITHDRAWAL.name(),
                "Withdrawal blocked! Account is Frozen",
                amount
        ));

        throw new IllegalStateException("Operation failed: Account is frozen and does not allow withdrawals.");
    }

    @Override
    public void close(Account account) {
        account.setState(new ClosedState());

        account.notifyObservers(new AccountEvent(
                account,
                "STATE_CHANGE",
                "Account closed from Frozen state",
                null
        ));

        PrintUtil.println("Account closed from Frozen state.");
    }

    @Override
    public void freeze(Account account) {
        PrintUtil.println("Notice: Account is already in Frozen state.");
    }

    @Override
    public void suspend(Account account) {
        account.setState(new SuspendedState());

        account.notifyObservers(new AccountEvent(
                account,
                "STATE_CHANGE",
                "Account moved from Frozen to Suspended",
                null
        ));

        PrintUtil.println("Account moved from Frozen to Suspended state.");
    }

    @Override
    public void activate(Account account) {
        account.setState(new ActiveState());

        account.notifyObservers(new AccountEvent(
                account,
                "STATE_CHANGE",
                "Account reactivated from Frozen",
                null
        ));

        PrintUtil.println("✅ Account has been reactivated.");
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
