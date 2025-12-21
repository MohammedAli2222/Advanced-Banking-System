package com.bank.utils;

import java.math.BigDecimal;

public class Money {
    private BigDecimal amount;
    private Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        if (amount == null) throw new IllegalArgumentException("Amount is required");
        if (currency == null) throw new IllegalArgumentException("Currency is required");
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.getCurrency())) {
            throw new IllegalArgumentException("Currencies do not match");
        }
        return new Money(this.amount.add(other.getAmount()), this.currency);
    }

    private void checkCurrencyMatch(Money other) {
        if (!this.currency.equals(other.getCurrency())) {
            throw new IllegalArgumentException("Currencies do not match");
        }
    }

    public Money subtract(Money other) {
        checkCurrencyMatch(other);
        return new Money(this.amount.subtract(other.getAmount()), this.currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}