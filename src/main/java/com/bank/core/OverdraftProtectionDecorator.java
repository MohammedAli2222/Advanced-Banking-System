package com.bank.core;

import com.bank.notifications.Observer;
import com.bank.utils.AccountEvent;
import com.bank.utils.Money;
import com.bank.utils.TransactionType;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Decorator لإضافة ميزة الحماية من السحب على المكشوف.
 * تم تعديله ليتوافق مع نظام الـ Composite (طريقة الأمان).
 */
public class OverdraftProtectionDecorator implements AccountComponent {
    // نغلف Account (الـ Leaf) لأن ميزة الـ Overdraft مرتبطة بحساب حقيقي
    private final Account delegate;
    private final Money overdraftLimit;

    public OverdraftProtectionDecorator(Account delegate, Money overdraftLimit) {
        this.delegate = delegate;
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void withdraw(Money amount) {
        if (!(delegate instanceof Account)) {
            throw new IllegalStateException("Delegate must be a concrete Account to use locking");
        }

        ReentrantLock lock = ((Account) delegate).getLock();
        lock.lock();
        try {
            // التحقق من الحالة (State)
            if (!delegate.getState().validateOperation(TransactionType.WITHDRAWAL)) {
                throw new IllegalStateException("Operation not allowed: " + delegate.getStateDescription());
            }

            java.math.BigDecimal current = delegate.getBalance().getAmount();
            java.math.BigDecimal newBalBd = current.subtract(amount.getAmount());
            java.math.BigDecimal allowedLimit = overdraftLimit.getAmount().negate();

            if (newBalBd.compareTo(allowedLimit) < 0) {
                throw new IllegalStateException("Overdraft limit exceeded!");
            }

            // تطبيق التغيير على الحساب الأصلي
            Money newBalance = new Money(newBalBd, delegate.getBalance().getCurrency());
            delegate.applyBalanceChange(newBalance);

            delegate.notifyObservers(new AccountEvent(
                    delegate,                              // الحساب
                    TransactionType.WITHDRAWAL.name(),     // نوع الحدث
                    "Withdrawal via OverdraftProtection",  // التفاصيل
                    amount                                 // المبلغ
            ));

        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deposit(Money amount) {
        delegate.deposit(amount);
    }

    @Override
    public Money getBalance() {
        return delegate.getBalance();
    }

    @Override
    public String getAccountNumber() {
        return delegate.getAccountNumber();
    }

    @Override
    public void addObserver(Observer observer) {
        delegate.addObserver(observer);
    }

    // دوال إضافية للوصول للخصائص الأصلية إذا لزم الأمر
    public Account getDelegate() {
        return delegate;
    }


        public Money getOverdraftLimit() {
            return this.overdraftLimit;
        }


    @Override
    public String toString() {
        return delegate.toString() + " [Overdraft Protected: " + overdraftLimit + "]";
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void print() {
        System.out.print("🛡️ [Overdraft Protected] ");
        delegate.print();
    }
}