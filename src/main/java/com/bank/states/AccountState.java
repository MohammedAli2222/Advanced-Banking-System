package com.bank.states;

import com.bank.core.Account;
import com.bank.utils.Money;
import com.bank.utils.TransactionType;

public interface AccountState {
    // إيداع
    void deposit(Account account, Money amount);

    // سحب
    void withdraw(Account account, Money amount);

    // إغلاق الحساب
    void close(Account account);

    // تجميد الحساب
    void freeze(Account account);

    // تعليق الحساب
    void suspend(Account account);

    // تفعيل الحساب
    void activate(Account account);

    // رسالة توضيحية للحالة (مفيدة للـ logging أو UI)
    String getStateDescription();

    boolean validateOperation(TransactionType type);
}