package com.bank.utils;

import java.math.BigDecimal;

public class Money {

    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        if (amount == null) throw new IllegalArgumentException("Amount is required");
        if (currency == null) throw new IllegalArgumentException("Currency is required");
        this.amount = amount;
        this.currency = currency;
    }

    // ================= FACTORY METHODS =================

    /**
     * إنشاء قيمة مالية صفرية لعملة محددة
     */
    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    // ================= GETTERS =================

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    // ================= OPERATIONS =================

    public Money add(Money other) {
        checkCurrencyMatch(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        checkCurrencyMatch(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    private void checkCurrencyMatch(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currencies do not match");
        }
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
