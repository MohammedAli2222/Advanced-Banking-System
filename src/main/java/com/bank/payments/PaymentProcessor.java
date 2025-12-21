/**
package com.bank.payments;

import com.bank.core.Account;
import com.bank.utils.Money;

public interface PaymentProcessor {
    /**
     * Process a payment/transfer via external gateway.
     * Return true if the external step was successful.
     * Note: ACID-level money transfer must still be coordinated by app (withdraw/deposit).

    boolean processPayment(Account from, Account to, Money amount);
}
*/