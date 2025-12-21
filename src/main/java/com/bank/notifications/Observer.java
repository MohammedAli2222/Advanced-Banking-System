package com.bank.notifications;

import com.bank.core.Account;
import com.bank.utils.AccountEvent;

public interface Observer {
    void update(AccountEvent event);
}