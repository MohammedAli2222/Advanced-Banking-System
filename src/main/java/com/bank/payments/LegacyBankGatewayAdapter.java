/**

package com.bank.payments;

import com.bank.core.Account;
import com.bank.utils.Money;

/**
 * Adapter for a legacy bank gateway (simulated).

public class LegacyBankGatewayAdapter implements PaymentProcessor {

    // Simulate legacy API integration
    @Override
    public boolean processPayment(Account from, Account to, Money amount) {
        // In real code: transform request to legacy API, sign, send, reconcile result.
        System.out.println("[LEGACY GATEWAY] Processing payment via legacy gateway: " +
                from.getAccountNumber() + " -> " + to.getAccountNumber() + " : " + amount);
        // Simulate success
        return true;
    }
}

 */