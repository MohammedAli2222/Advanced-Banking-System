package com.bank.notifications;

import com.bank.utils.AccountEvent;

public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(AccountEvent event);
}