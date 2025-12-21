package com.bank.transactions;

import com.bank.core.LoanAccount;
import com.bank.utils.TransactionType;

import java.math.BigDecimal;

public class SufficientBalanceValidator extends AbstractTransactionHandler {

    @Override
    protected boolean validate(Transaction transaction) {
        if (transaction.getType() == TransactionType.WITHDRAWAL) {
            if (transaction.getAccount() instanceof LoanAccount) {
                return true;
            }
            BigDecimal newBalance = transaction.getAccount().getBalance().getAmount()
                    .subtract(transaction.getAmount().getAmount());
            return newBalance.compareTo(BigDecimal.ZERO) >= 0;
        }
        return true;
    }
    @Override
    protected String getRejectionReason() {
        return "Insufficient balance";
    }

    @Override
    protected void execute(Transaction transaction) {}
}