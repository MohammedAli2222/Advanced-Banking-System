package com.bank.core;

import com.bank.notifications.Observer;
import com.bank.utils.Money;
import com.bank.utils.Currency;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CompositeAccount implements AccountComponent {
    // استخدمنا groupName كما هو في الكود الخاص بك
    private final String groupName;
    private final List<AccountComponent> children = new ArrayList<>();

    public CompositeAccount(String groupName) {
        this.groupName = groupName;
    }

    // --- تنفيذ دوال AccountComponent ---

    @Override
    public String getName() {
        // تصحيح: إرجاع groupName بدلاً من name
        return this.groupName;
    }

    @Override
    public String getAccountNumber() {
        return "GROUP: " + groupName;
    }

    @Override
    public Money getBalance() {
        BigDecimal total = BigDecimal.ZERO;
        Currency currency = Currency.USD;

        for (AccountComponent child : children) {
            total = total.add(child.getBalance().getAmount());
        }
        return new Money(total, currency);
    }

    @Override
    public void print() {
        System.out.println("\n📂 Portfolio Group: " + getName() + " | Total: " + getBalance());
        for (AccountComponent child : children) {
            child.print(); // استدعاء تكراري للطباعة
        }
    }

    // --- دوال الإدارة الخاصة بالـ Composite ---

    public void add(AccountComponent component) {
        children.add(component);
    }

    public void remove(AccountComponent component) {
        children.remove(component);
    }

    public List<AccountComponent> getChildren() {
        return new ArrayList<>(children);
    }

    // --- العمليات المالية (ممنوعة على مستوى المجموعة مباشرة) ---

    @Override
    public void deposit(Money amount) {
        throw new UnsupportedOperationException("Direct deposit to a group is not allowed.");
    }

    @Override
    public void withdraw(Money amount) {
        throw new UnsupportedOperationException("Direct withdrawal from a group is not allowed.");
    }

    // --- نظام المراقبين (Observers) ---

    @Override
    public void addObserver(Observer observer) {
        for (AccountComponent child : children) {
            child.addObserver(observer);
        }
    }

    @Override
    public String toString() {
        return "CompositeAccount[Name: " + groupName + ", Members: " + children.size() + ", Total Balance: " + getBalance() + "]";
    }

}