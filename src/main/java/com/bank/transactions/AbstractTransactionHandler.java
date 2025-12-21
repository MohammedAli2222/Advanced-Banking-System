package com.bank.transactions;

import com.bank.utils.TransactionStatus;

public abstract class AbstractTransactionHandler implements TransactionHandler {
    protected TransactionHandler nextHandler;

    @Override
    public void setNextHandler(TransactionHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handle(Transaction transaction) {
        if (validate(transaction)) {
            if (nextHandler != null) {
                nextHandler.handle(transaction);
            } else {
                transaction.setStatus(TransactionStatus.COMPLETED);
                execute(transaction);
                System.out.println("Transaction approved and completed: " + transaction);
            }
        } else {
            transaction.setStatus(TransactionStatus.FAILED);
            System.out.println("Transaction rejected: " + getRejectionReason());
        }
    }

    protected abstract boolean validate(Transaction transaction);
    protected abstract String getRejectionReason();
    protected abstract void execute(Transaction transaction);  // تنفيذ الإيداع/سحب الفعلي
}