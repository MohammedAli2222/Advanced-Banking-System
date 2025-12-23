package com.bank.utils;

import com.bank.core.AccountComponent;

public class AccountEvent {

    private final AccountComponent account;
    private final String eventType;
    private final String details;
    private final Money amount; // إذا موجود

    public AccountEvent(AccountComponent account, String eventType, String details, Money amount) {
        this.account = account;
        this.eventType = eventType;
        this.details = details;
        this.amount = amount;
    }

    public AccountComponent getAccount() {
        return account;
    }

    public String getEventType() {
        return eventType;
    }

    public String getDetails() {
        return details;
    }

    public Money getAmount() {
        return amount;
    }
}
