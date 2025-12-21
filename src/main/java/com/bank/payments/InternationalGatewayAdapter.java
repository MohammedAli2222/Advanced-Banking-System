package com.bank.payments;

import com.bank.core.Account;
import com.bank.utils.Money;

/**
 * Adapter for an international/fictitious gateway (simulated).
 */
public class InternationalGatewayAdapter implements PaymentProcessor {

    @Override
    public boolean processPayment(Account from, Account to, Money amount) {
        System.out.println("[INTL GATEWAY] Processing international transfer: " +
                from.getAccountNumber() + " -> " + to.getAccountNumber() + " : " + amount);
        // Here you could do currency conversion, SWIFT formatting, etc.
        return true;
    }
}