package com.bank.states;

import com.bank.core.Account;
import com.bank.core.LoanAccount;
import com.bank.utils.Money;
import com.bank.utils.TransactionType;
import com.bank.utils.PrintUtil;

import java.math.BigDecimal;

/**
 * Active state: يسمح بالعمليات الاعتيادية.
 */
public class ActiveState implements AccountState {

    @Override
    public void deposit(Account account, Money amount) {
        account.applyBalanceChange(account.getBalance().add(amount));
        PrintUtil.println("Deposit successful in Active state: " + amount);
    }

    @Override
    public void withdraw(Account account, Money amount) {
        Money newBalance = account.getBalance().subtract(amount);
        if (account instanceof LoanAccount) {
            account.applyBalanceChange(newBalance);
            PrintUtil.println("Borrow successful in Loan: " + amount);
        } else {
            if (newBalance.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException("Insufficient balance");
            }
            account.applyBalanceChange(newBalance);
            PrintUtil.println("Withdrawal successful: " + amount);
        }
    }

    @Override
    public void close(Account account) {
        account.setState(new ClosedState());
        PrintUtil.println("Account closed.");
    }

    @Override
    public void freeze(Account account) {
        account.setState(new FrozenState());
        PrintUtil.println("Account frozen.");
    }

    @Override
    public void suspend(Account account) {
        account.setState(new SuspendedState());
        PrintUtil.println("Account suspended.");
    }

    @Override
    public void activate(Account account) {
        PrintUtil.println("Account is already active.");
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