package com.bank.core;

import com.bank.notifications.Observer;
import com.bank.utils.AccountEvent;
import com.bank.utils.Money;
import com.bank.utils.TransactionType;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Decorator that adds overdraft protection feature.
 * عندما يسمح السحب (ضمن الحد)، يقوم الديكوراتور بتطبيق تغيير الرصيد بنفسه
 * عن طريق delegate.applyBalanceChange(...) بدلاً من التفويض للـ State الذي
 * قد يمنع الرصيد السلبي.
 */
public class OverdraftProtectionDecorator extends Account {
    private final Account delegate;
    private final Money overdraftLimit; // positive value e.g., 100.00

    public OverdraftProtectionDecorator(Account delegate, Money overdraftLimit) {
        // نمرّر قيم للـ super لكن كل السلوك الفعلي يُفوّض للـ delegate
        super(delegate.getAccountNumber(), delegate.getBalance(), delegate.getStrategy(), delegate.getCustomer());
        this.delegate = delegate;
        this.overdraftLimit = overdraftLimit;
    }

    private ReentrantLock lock() {
        return delegate.getLock();
    }

    @Override
    public void deposit(Money amount) {
        lock().lock();
        try {
            // نحافظ على تفويض الإيداع للـ delegate لأنه يتعامل مع القواعد
            delegate.deposit(amount);
        } finally {
            lock().unlock();
        }
    }

    @Override
    public void withdraw(Money amount) {
        lock().lock();
        try {
            // تحقق من أن الحالة الحالية تسمح بعملية سحب (مثلاً ليست Frozen)
            if (!delegate.getState().validateOperation(TransactionType.WITHDRAWAL)) {
                throw new IllegalStateException("Operation not allowed in current state: " + delegate.getStateDescription());
            }

            // حساب الرصيد الجديد بعد السحب
            java.math.BigDecimal current = delegate.getBalance().getAmount();
            java.math.BigDecimal newBalBd = current.subtract(amount.getAmount());
            java.math.BigDecimal allowed = overdraftLimit.getAmount().negate(); // e.g. allowed = -100

            // تحقق حد السحب على المكشوف
            if (newBalBd.compareTo(allowed) < 0) {
                throw new IllegalStateException("Overdraft limit exceeded");
            }

            // تطبيق التغيير مباشرة على الـ delegate (بما أن State قد تمنع السالب)
            Money newBalance = new Money(newBalBd, delegate.getBalance().getCurrency());
            delegate.applyBalanceChange(newBalance);

            // إشعار الملاحظين بحدث السحب
            delegate.notifyObservers(new AccountEvent(
                    TransactionType.WITHDRAWAL.name(),
                    delegate,
                    amount,
                    "Withdrawal via OverdraftProtectionDecorator"
            ));
        } finally {
            lock().unlock();
        }
    }

    // التفويض لباقي الدوال
    @Override public String getAccountNumber() { return delegate.getAccountNumber(); }
    @Override public Money getBalance() { return delegate.getBalance(); }
    @Override public com.bank.states.AccountState getState() { return delegate.getState(); }
    @Override public String getStateDescription() { return delegate.getStateDescription(); }
    @Override public com.bank.utils.DateTime getCreationDate() { return delegate.getCreationDate(); }
    @Override public void setState(com.bank.states.AccountState state) { delegate.setState(state); }
    @Override public void setStrategy(com.bank.strategies.AccountStrategy strategy) { delegate.setStrategy(strategy); }
    @Override public void notifyObservers(AccountEvent event) { delegate.notifyObservers(event); }
    @Override public void close() { delegate.close(); }
    @Override public void freeze() { delegate.freeze(); }
    @Override public void suspend() { delegate.suspend(); }
    @Override public void activate() { delegate.activate(); }
    @Override public void getRecommendations() { delegate.getRecommendations(); }
    @Override public com.bank.customers.Customer getCustomer() { return delegate.getCustomer(); }
    @Override public void setCustomer(com.bank.customers.Customer customer) { delegate.setCustomer(customer); }
    @Override public com.bank.utils.Money calculateInterest() { return delegate.calculateInterest(); }
    @Override public com.bank.utils.Money applyFees() { return delegate.applyFees(); }
    @Override public void addObserver(Observer observer) { delegate.addObserver(observer); }
    @Override public void removeObserver(Observer observer) { delegate.removeObserver(observer); }
    @Override public String toString() { return delegate.toString() + " [OverdraftProtection: " + overdraftLimit + "]"; }
    @Override public com.bank.strategies.AccountStrategy getStrategy() { return delegate.getStrategy(); }
    @Override public void addChild(Account child) { delegate.addChild(child); }
    @Override public void removeChild(Account child) { delegate.removeChild(child); }
    @Override public java.util.List<Account> getChildren() { return delegate.getChildren(); }
    @Override public Account getParent() { return delegate.getParent(); }
    @Override public java.util.concurrent.locks.ReentrantLock getLock() { return delegate.getLock(); }
}