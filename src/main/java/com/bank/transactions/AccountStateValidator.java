package com.bank.transactions;

import com.bank.utils.TransactionType;

public class AccountStateValidator extends AbstractTransactionHandler {

    @Override
    protected boolean validate(Transaction transaction) {
        // Use the AccountState.validateOperation rather than string matching
        boolean allowedByState = transaction.getAccount().getState().validateOperation(transaction.getType());
        if (!allowedByState) {
            return false;
        }

        // Additional check: if withdrawal/transfer (outgoing money) and state forbids it, deny
        boolean isWithdrawal = transaction.getType() == TransactionType.WITHDRAWAL ||
                transaction.getType() == TransactionType.TRANSFER;

        if (isWithdrawal) {
            // For example Frozen allows only deposits (validateOperation above will reject withdrawal)
            // Keep this explicit for clarity
            return transaction.getAccount().getState().validateOperation(transaction.getType());
        }

        return true;
    }

    @Override
    protected String getRejectionReason() {
        return "Account state does not allow this transaction";
    }

    @Override
    protected void execute(Transaction transaction) {
        // No-op validator
    }
}