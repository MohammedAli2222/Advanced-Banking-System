package com.bank.reports;

import com.bank.core.Account;

public class ReportLeaf implements ReportComponent {
    private Account account;

    public ReportLeaf(Account account) {
        this.account = account;
    }

    @Override
    public String generate() {
        String dateStr = (account.getCreationDate() != null)
                ? account.getCreationDate().toString()
                : "N/A";

        return "=== Account Report ===\n" +
                "Account Number: " + account.getAccountNumber() + "\n" +
                "Balance: " + account.getBalance() + "\n" +
                "State: " + account.getStateDescription() + "\n" +
                "Creation Date: " + dateStr + "\n";
    }

    @Override
    public void add(ReportComponent component) {
    }

    @Override
    public void remove(ReportComponent component) {
    }
}