package com.bank.notifications;

import com.bank.utils.AccountEvent;

public class SmsNotifier implements Observer {

    private String customerPhone;

    public SmsNotifier(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    @Override
    public void update(AccountEvent event) {
        System.out.println("[SMS] Sent to " + customerPhone +
                " | Account: " + event.getAccount().getAccountNumber() +
                " | Event: " + event.getEventType() +
                " | " + event.getDetails());
    }
}