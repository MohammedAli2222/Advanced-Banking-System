package com.bank.core;

import com.bank.strategies.InvestmentStrategy;
import com.bank.utils.Money;

public class InvestmentAccount extends Account {
    private Portfolio portfolio;

    public InvestmentAccount(String accountNumber, Money initialBalance) {
        // نمرر الـ accountNumber للأب
        super(accountNumber, initialBalance, new InvestmentStrategy());
        this.portfolio = new Portfolio();
    }

    @Override
    public Money getBalance() {
        // استخدم رصيد الأب (الحساب) لكي ترى مبالغ الإيداع في الـ Demo
        return super.getBalance();
    }

    public void addInvestment(Asset asset) {
        portfolio.addAsset(asset);
    }

    public void updatePortfolio() {
        // التصحيح هنا: استخدام getAccountNumber() بدلاً من getAccountId()
        System.out.println("Updating Portfolio for account: " + getAccountNumber());
    }
}