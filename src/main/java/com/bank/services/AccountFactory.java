package com.bank.services;

import com.bank.core.*;
import com.bank.utils.Money;
import com.bank.utils.Currency;
import java.math.BigDecimal;

/**
 * مصنع الحسابات المعدل ليدعم نمط الـ Composite والخصائص الجديدة.
 */
public class AccountFactory {

    public static AccountComponent createAccount(String type, String id, Money initialBalance) {
        Currency currency = initialBalance.getCurrency();

        switch (type.toUpperCase()) {
            case "SAVINGS":
                // تمرير نسبة فائدة افتراضية 0.05 (5%)
                return new SavingsAccount(id, initialBalance, 0.05);

            case "CHECKING":
                // تمرير سقف سحب افتراضي (صفر حالياً)
                return new CheckingAccount(id, initialBalance, new Money(BigDecimal.ZERO, currency));

            case "INVESTMENT":
                return new InvestmentAccount(id, initialBalance);

            case "LOAN":
                // تمرير نسبة فائدة قرض افتراضية 0.10 (10%)
                return new LoanAccount(id, initialBalance, 0.10);

            case "GROUP":
            case "COMPOSITE":
                return new CompositeAccount(id);

            default:
                throw new IllegalArgumentException("Unknown component type: " + type);
        }
    }

    public static AccountComponent createAccount(String type, String id) {
        return createAccount(type, id, new Money(BigDecimal.ZERO, Currency.USD));
    }
}