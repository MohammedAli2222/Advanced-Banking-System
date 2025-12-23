package com.bank.strategies;

import com.bank.utils.Money;
import com.bank.utils.TransactionType;
import com.bank.utils.Currency; // تأكد من استيراد الـ Enum
import java.math.BigDecimal;

public class InvestmentStrategy implements AccountStrategy {

    @Override
    public Money calculateInterest(Money balance, double interestRate) {
        BigDecimal interest = balance.getAmount().multiply(BigDecimal.valueOf(interestRate));
        return new Money(interest, balance.getCurrency());
    }

    @Override
    public Money applyFees(Money balance) {
        BigDecimal fee = balance.getAmount().multiply(new BigDecimal("0.01"));
        return new Money(fee, balance.getCurrency());
    }

    @Override
    public Money withdrawRules(Money amount, Money balance, Money overdraftLimit) {
        // رسوم سحب مبكر 2% (تُرجع كرسوم إضافية يتم خصمها)
        BigDecimal earlyFee = amount.getAmount().multiply(new BigDecimal("0.02"));
        return new Money(earlyFee, amount.getCurrency());
    }

    @Override
    public Money depositRules(Money amount) {
        return amount;
    }

    @Override
    public Money applyRules(Money amount, TransactionType type, Money balance, double interestRate, Money overdraftLimit) {
        if (type == TransactionType.DEPOSIT) {
            return amount; // إرجاع المبلغ كامل ليتم إضافته للرصيد
        }
        if (type == TransactionType.WITHDRAWAL) {
            // ملاحظة: استراتيجية الاستثمار عادة لا تسمح بسحب مكشوف (Overdraft)
            // لذا نكتفي بحساب الرسوم
            return withdrawRules(amount, balance, overdraftLimit);
        }
        // القيمة الافتراضية في حال وجود أنواع عمليات أخرى
        return new Money(BigDecimal.ZERO, amount.getCurrency());
    }
}