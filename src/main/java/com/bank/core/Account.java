package com.bank.core;

import com.bank.customers.Customer;
import com.bank.notifications.Observer;
import com.bank.recommendations.RecommendationEngine;
import com.bank.recommendations.SpendingStrategy;
import com.bank.states.AccountState;
import com.bank.states.ActiveState;
import com.bank.strategies.AccountStrategy;
import com.bank.utils.AccountEvent;
import com.bank.utils.Money;
import com.bank.utils.DateTime;
import com.bank.utils.TransactionType;
import com.bank.utils.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract Account class implementing AccountComponent
 */
public abstract class Account implements AccountComponent {

    private static final Logger logger = LoggerFactory.getLogger(Account.class);

    private final List<Observer> observers = new ArrayList<>();
    private final RecommendationEngine recommendationEngine = new RecommendationEngine(new SpendingStrategy());

    protected Money overdraftLimit = new Money(java.math.BigDecimal.ZERO, Currency.USD);

    private Customer customer;
    private final String accountNumber;
    protected Money balance;
    private AccountState state;
    private AccountStrategy strategy;
    private final DateTime creationDate;
    private final ReentrantLock lock = new ReentrantLock();

    // ========================= Constructors =========================
    public Account(String accountNumber, Money initialBalance, AccountStrategy strategy) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.strategy = strategy;
        this.state = new ActiveState();
        this.creationDate = new DateTime();
    }

    public Account(String accountNumber, Money initialBalance, AccountStrategy strategy, Customer customer) {
        this(accountNumber, initialBalance, strategy);
        this.customer = customer;
    }

    // ========================= Basic Info =========================
    @Override
    public String getAccountNumber() { return accountNumber; }

    @Override
    public String getName() { return accountNumber; }

    public DateTime getCreationDate() { return creationDate; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public AccountStrategy getStrategy() { return strategy; }
    public void setStrategy(AccountStrategy strategy) { this.strategy = strategy; }

    public AccountState getState() { return state; }
    public String getStateDescription() { return state.getStateDescription(); }
    public void setState(AccountState state) {
        this.state = state;
        logger.info("Account {} state changed to: {}", accountNumber, state.getStateDescription());
    }

    public ReentrantLock getLock() { return lock; }

    public void setOverdraftLimit(Money overdraftLimit) { this.overdraftLimit = overdraftLimit; }
    public Money getOverdraftLimitInternal() { return this.overdraftLimit; }

    @Override
    public synchronized Money getBalance() { return balance; }

    // ========================= Transactions =========================
    @Override
    public synchronized void deposit(Money amount) {
        state.deposit(this, amount);
    }

    @Override
    public synchronized void withdraw(Money amount) {
        state.withdraw(this, amount);
    }

    public synchronized void applyBalanceChange(Money amount, TransactionType type) {
        Money extraAmount = strategy.applyRules(
                amount,
                type,
                this.balance,
                getInterestRateInternal(),
                getOverdraftLimitInternal()
        );

        if (type == TransactionType.DEPOSIT) {
            this.balance = this.balance.add(amount).add(extraAmount);
        } else if (type == TransactionType.WITHDRAWAL) {
            this.balance = this.balance.subtract(amount).subtract(extraAmount);
        }
    }

    public synchronized void applyBalanceChange(Money newBalance) {
        this.balance = newBalance;
    }

    public double getInterestRateInternal() { return 0.0; }

    // ========================= Observers =========================
    @Override
    public void addObserver(Observer observer) {
        if (observer != null) observers.add(observer);
    }

    public void notifyObservers(AccountEvent event) {
        for (Observer observer : observers) {
            try {
                observer.update(event);
            } catch (Exception e) {
                logger.warn("Observer failed: {}", e.getMessage());
            }
        }
    }

    // ========================= State Changes =========================
    public void close() {
        state.close(this);
        notifyObservers(new AccountEvent(
                this,
                "STATE_CHANGE",
                "Account closed",
                null
        ));
    }

    public synchronized void freeze() {
        state.freeze(this);
        notifyObservers(new AccountEvent(
                this,
                "STATE_CHANGE",
                "Account frozen",
                null
        ));
    }

    public synchronized void activate() {
        state.activate(this);
        notifyObservers(new AccountEvent(
                this,
                "STATE_CHANGE",
                "Account activated",
                null
        ));
    }

    // ========================= Utilities =========================
    @Override
    public void print() {
        System.out.println("   📄 Account: [" + accountNumber + "] | Balance: " + balance
                + " | State: " + state.getStateDescription());
    }

    public void getRecommendations() {
        logger.info("Recommendations for account {}: {}", accountNumber, recommendationEngine.generate(customer));
    }
}
