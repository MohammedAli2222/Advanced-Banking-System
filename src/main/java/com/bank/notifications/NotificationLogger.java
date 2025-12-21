package com.bank.notifications;

import com.bank.utils.AccountEvent;

public class NotificationLogger implements Observer {

    @Override
    public void update(AccountEvent event) {
        System.out.println("[LOG] Account " + event.getAccount().getAccountNumber() +
                " | Event: " + event.getEventType() +
                " | Balance: " + event.getAccount().getBalance() +
                " | Time: " + new com.bank.utils.DateTime());
    }
}