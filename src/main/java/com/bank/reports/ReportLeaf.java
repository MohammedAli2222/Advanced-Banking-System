package com.bank.reports;

import com.bank.core.Account;

public class ReportLeaf implements ReportComponent {
    private Account account;

    public ReportLeaf(Account account) {
        this.account = account;
    }

    @Override
    public String generate() {
        return "=== Account Report ===\n" +
                "Account Number: " + account.getAccountNumber() + "\n" +
                "Balance: " + account.getBalance() + "\n" +
                "State: " + account.getStateDescription() + "\n" +
                "Creation Date: " + account.getCreationDate() + "\n";
    }

    @Override
    public void add(ReportComponent component) {
    }

    @Override
    public void remove(ReportComponent component) {
    }
}