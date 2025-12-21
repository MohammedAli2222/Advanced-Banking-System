package com.bank.utils;

import com.bank.core.Account;

public class AccountEvent {
    private String eventType;
    private Account account;
    private Money amount;
    private String details;

    public AccountEvent(String eventType, Account account, Money amount, String details) {
        this.eventType = eventType;
        this.account = account;
        this.amount = amount;
        this.details = details;
    }

    public String getEventType() { return eventType; }
    public Account getAccount() { return account; }
    public Money getAmount() { return amount; }
    public String getDetails() { return details; }
}