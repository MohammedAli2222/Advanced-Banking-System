package com.bank.core;

import com.bank.notifications.Observer;
import com.bank.states.AccountState;
import com.bank.utils.AccountEvent;
import com.bank.utils.Money;

/**
 * الواجهة المشتركة لنمط الـ Composite (Safety).
 * تمثل حسابًا أو مجموعة حسابات.
 */
public interface AccountComponent {

    // ================= BASIC INFO =================
    String getAccountNumber();
    String getName();

    // ================= BALANCE OPS =================
    Money getBalance();
    void deposit(Money amount);
    void withdraw(Money amount);

    // ================= OBSERVER SUPPORT =================

    /**
     * إضافة مراقب (افتراضيًا لا يفعل شيئًا – للحفاظ على Safety)
     */
    default void addObserver(Observer observer) {
        // no-op
    }

    /**
     * إشعار المراقبين (افتراضيًا لا يفعل شيئًا)
     */
    default void notifyObservers(AccountEvent event) {
        // no-op
    }

    // ================= OVERDRAFT SUPPORT =================

    /**
     * حد السحب المكشوف (افتراضيًا 0)
     */
    default Money getOverdraftLimit() {
        return Money.zero(getBalance().getCurrency());
    }

    default AccountState getState() {
        return null; // أو رمي استثناء افتراضي
    }


    // ================= COMPOSITE OPS =================
    void print();
}
