package com.bank.transactions;

import com.bank.utils.AccountEvent;
import com.bank.utils.TransactionStatus;
import com.bank.utils.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class FinalExecutor extends AbstractTransactionHandler {

    private static final Logger logger = LoggerFactory.getLogger(FinalExecutor.class);

    @Override
    protected boolean validate(Transaction transaction) {
        return true;
    }

    @Override
    protected String getRejectionReason() {
        return "";
    }

    @Override
    protected void execute(Transaction transaction) {
        MDC.put("txId", transaction.getTransactionId());
        MDC.put("account", transaction.getAccount().getAccountNumber());
        try {
            if (transaction.getType() == TransactionType.DEPOSIT) {
                transaction.getAccount().deposit(transaction.getAmount());
            } else if (transaction.getType() == TransactionType.WITHDRAWAL ||
                    transaction.getType() == TransactionType.TRANSFER ||
                    transaction.getType() == TransactionType.PAYMENT) {
                transaction.getAccount().withdraw(transaction.getAmount());
            }

            // استدعاء notifyObservers بعد التنفيذ الناجح
            transaction.getAccount().notifyObservers(new AccountEvent(
                    transaction.getType().name(),
                    transaction.getAccount(),
                    transaction.getAmount(),
                    transaction.getType() + " completed successfully"
            ));

            transaction.setStatus(TransactionStatus.COMPLETED);
            logger.info("Transaction approved and completed: {}", transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            logger.error("Transaction failed during execution: {} - error: {}", transaction, e.getMessage(), e);
        } finally {
            MDC.remove("txId");
            MDC.remove("account");
        }
    }
}