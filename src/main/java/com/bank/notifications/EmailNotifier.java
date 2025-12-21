package com.bank.notifications;

import com.bank.utils.AccountEvent;

public class EmailNotifier implements Observer {

    private String customerEmail;

    public EmailNotifier(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    @Override
    public void update(AccountEvent event) {
        System.out.println("[EMAIL] Sent to " + customerEmail +
                " | Account: " + event.getAccount().getAccountNumber() +
                " | Event: " + event.getEventType() +
                " | " + event.getDetails() +
                " | Balance: " + event.getAccount().getBalance());
    }
}