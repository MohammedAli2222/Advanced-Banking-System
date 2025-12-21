package com.bank.transactions;

import java.math.BigDecimal;

public class ManagerApprovalValidator extends AbstractTransactionHandler {
    private static final BigDecimal HIGH_AMOUNT = new BigDecimal("5000.00");

    @Override
    protected boolean validate(Transaction transaction) {
        if (transaction.getAmount().getAmount().compareTo(HIGH_AMOUNT) > 0) {
            System.out.println("Manager approval required for large transaction (simulated approval)");
            return true;
        }
        return true;
    }

    @Override
    protected String getRejectionReason() {
        return "Requires manager approval";
    }

    @Override
    protected void execute(Transaction transaction) {
    }
}