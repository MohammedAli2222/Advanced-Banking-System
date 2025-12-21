package com.bank.notifications;

import com.bank.utils.AccountEvent;

public class AppNotifier implements Observer {

    @Override
    public void update(AccountEvent event) {
        System.out.println("[APP NOTIFICATION] Account " + event.getAccount().getAccountNumber() +
                " | Event: " + event.getEventType() +
                " | " + event.getDetails() +
                " | Balance: " + event.getAccount().getBalance());
    }
}