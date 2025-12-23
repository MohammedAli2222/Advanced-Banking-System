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
        // 1. التحقق من شروط الـ Handler الحالي
        if (validate(transaction)) {
            // 2. إذا وجد Handler تالٍ، نمرر المعاملة له
            if (nextHandler != null) {
                nextHandler.handle(transaction);
            } else {
                // 3. إذا وصلنا لنهاية السلسلة (FinalExecutor)، ننفذ العملية
                execute(transaction);
            }
        } else {
            // 4. إذا فشل التحقق في أي مرحلة، نوقف السلسلة فوراً
            transaction.setStatus(TransactionStatus.FAILED);
            // يمكن إضافة إشعار هنا أو Log للسبب
            System.err.println("Transaction " + transaction.getTransactionId() +
                    " halted at " + this.getClass().getSimpleName() +
                    ": " + getRejectionReason());
        }
    }

    protected abstract boolean validate(Transaction transaction);
    protected abstract String getRejectionReason();
    protected abstract void execute(Transaction transaction);
}