package com.bank.transactions;

import com.bank.utils.AccountEvent;
import com.bank.utils.TransactionStatus;
import com.bank.utils.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FinalExecutor: آخر عنصر في سلسلة الـ TransactionHandler
 * يقوم بتنفيذ المعاملة فعليًا على الحسابات وإشعار المراقبين.
 */
public class FinalExecutor extends AbstractTransactionHandler {

    private static final Logger logger = LoggerFactory.getLogger(FinalExecutor.class);

    @Override
    protected boolean validate(Transaction transaction) {
        // آخر عنصر في السلسلة، دائمًا يُسمح بالمرور
        return true;
    }

    @Override
    protected String getRejectionReason() {
        return "";
    }

    @Override
    protected void execute(Transaction transaction) {

        try {
            // ========================= تنفيذ المعاملة =========================
            if (transaction.getType() == TransactionType.DEPOSIT) {
                transaction.getAccount().deposit(transaction.getAmount());
            } else {
                // سحب أو تحويل
                transaction.getAccount().withdraw(transaction.getAmount());

                // تحويل إلى حساب الوجهة
                if (transaction.getType() == TransactionType.TRANSFER &&
                        transaction.getDestinationAccount() != null) {
                    transaction.getDestinationAccount().deposit(transaction.getAmount());
                }
            }

            // ========================= إشعار المراقبين =========================
            AccountEvent event = new AccountEvent(
                    transaction.getAccount(),                  // الحساب
                    transaction.getType().name(),              // نوع المعاملة
                    "Transaction executed successfully",       // التفاصيل
                    transaction.getAmount()                    // المبلغ
            );

            transaction.getAccount().notifyObservers(event);

            // ========================= تحديث الحالة =========================
            transaction.setStatus(TransactionStatus.COMPLETED);

            logger.info("Transaction {} completed successfully.", transaction.getTransactionId());

        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            logger.error("Transaction {} failed: {}", transaction.getTransactionId(), e.getMessage());
            throw e;
        }
    }
}
