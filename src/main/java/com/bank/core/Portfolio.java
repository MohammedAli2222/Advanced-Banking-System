package com.bank.core;

import com.bank.utils.Money;
import com.bank.utils.Currency;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Portfolio {
    private List<Asset> assets = new ArrayList<>();

    public void addAsset(Asset asset) {
        assets.add(asset);
    }

    public void removeAsset(Asset asset) {
        assets.remove(asset);
    }

    public Money calculateValue() {
        BigDecimal total = BigDecimal.ZERO;
        Currency currency = Currency.USD; // افتراضي

        for (Asset asset : assets) {
            total = total.add(asset.getValue().getAmount());
            currency = asset.getValue().getCurrency();
        }
        return new Money(total, currency);
    }
}