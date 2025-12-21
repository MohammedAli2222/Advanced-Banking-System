package com.bank.transactions;

import java.math.BigDecimal;

public class DailyLimitValidator extends AbstractTransactionHandler {
    private static final BigDecimal DAILY_LIMIT = new BigDecimal("10000.00");

    @Override
    protected boolean validate(Transaction transaction) {
        return transaction.getAmount().getAmount().compareTo(DAILY_LIMIT) <= 0;
    }

    @Override
    protected String getRejectionReason() {
        return "Exceeds daily limit";
    }

    @Override
    protected void execute(Transaction transaction) {
    }
}