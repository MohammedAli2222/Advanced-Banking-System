package com.bank.notifications;

import com.bank.utils.AccountEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple in-memory transaction/event logger that also acts as an Observer.
 * It records AccountEvent objects in insertion order and exposes getHistory().
 */
public class TransactionLogger implements Observer {
    private final List<AccountEvent> history = new ArrayList<>();

    @Override
    public synchronized void update(AccountEvent event) {
        history.add(event);
        // a concise log line for runtime visibility
        System.out.println("[TRANSACTION LOGGER] " + event.getEventType()
                + " | Account: " + (event.getAccount() != null ? event.getAccount().getAccountNumber() : "N/A")
                + " | Details: " + event.getDetails()
                + " | Amount: " + (event.getAmount() != null ? event.getAmount() : "N/A"));
    }

    public synchronized List<AccountEvent> getHistory() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }
}