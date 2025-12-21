package com.bank.strategies;

import com.bank.utils.Money;
import com.bank.utils.TransactionType;

public interface AccountStrategy {
    // حساب الفوائد على الرصيد
    Money calculateInterest(Money balance);

    // تطبيق الرسوم (مثل رسوم شهرية أو على السحب)
    Money applyFees(Money balance);

    // قواعد السحب (مثل حد أدنى أو رسوم إضافية) - يرجع الرسوم الإضافية إذا وجدت
    Money withdrawRules(Money amount, Money balance);

    // قواعد الإيداع (بعض الحسابات قد تمنح bonus)
    Money depositRules(Money amount);

    Money applyRules(Money amount, TransactionType type, Money balance);
}