package com.bank.core;

import com.bank.utils.AssetType;
import com.bank.utils.Money;
import java.math.BigDecimal;

public class Asset {
    private String assetId;
    private String name;
    private AssetType type;
    private double quantity;
    private Money pricePerUnit;

    public Asset(String assetId, String name, AssetType type, double quantity, Money pricePerUnit) {
        this.assetId = assetId;
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    public Money getValue() {
        BigDecimal total = pricePerUnit.getAmount().multiply(BigDecimal.valueOf(quantity));
        return new Money(total, pricePerUnit.getCurrency());
    }

    public void buy(double quantity) {
        this.quantity += quantity;
    }

    public void sell(double quantity) {
        if (this.quantity >= quantity) {
            this.quantity -= quantity;
        }
    }
}