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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Base abstract Account class.
 * يدير: القفل، الـ composite (parent/children)، قائمة الملاحظين (observers)، وحالة الحساب.
 *
 * ملاحظة تصميمية مهمة:
 * - في هذا التصميم، الـ Account يفوض منطق تغيير الرصيد إلى AccountState (مثل ActiveState).
 * - لذلك State.deposit/withdraw مسؤولة عن تعديل balance (والـ Account لا تعدّل الرصيد مرتين).
 */
public abstract class Account {
    private static final Logger logger = LoggerFactory.getLogger(Account.class);

    private final List<Observer> observers = new ArrayList<>();
    private final RecommendationEngine recommendationEngine = new RecommendationEngine(new SpendingStrategy());
    private Customer customer;
    private final String accountNumber;
    private Money balance;
    private AccountState state;
    private AccountStrategy strategy;
    private final DateTime creationDate;

    // Lock for concurrency
    private final ReentrantLock lock = new ReentrantLock();

    // Composite support
    private Account parent;
    private final List<Account> children = new ArrayList<>();

    // Constructor
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

    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public synchronized Money getBalance() {
        return balance;
    }

    // package-private setter for State implementations to update balance
    synchronized void internalSetBalance(Money newBalance) {
        this.balance = newBalance;
    }

    public AccountState getState() {
        return state;
    }

    public String getStateDescription() {
        return state.getStateDescription();
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    // Lock access
    public ReentrantLock getLock() {
        return lock;
    }


    public void addChild(Account child) {
        if (child == null) return;
        synchronized (children) {
            children.add(child);
            child.parent = this;
        }
    }

    public void removeChild(Account child) {
        if (child == null) return;
        synchronized (children) {
            children.remove(child);
            if (child.parent == this) child.parent = null;
        }
    }

    public List<Account> getChildren() {
        synchronized (children) {
            return new ArrayList<>(children);
        }
    }

    public Account getParent() {
        return parent;
    }

    // Setters
    public void setState(AccountState state) {
        this.state = state;
        logger.info("Account {} state changed to: {}", accountNumber, state == null ? "null" : state.getStateDescription());
    }

    // Allow setting customer after creation
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public AccountStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(AccountStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Deposit: يفوض إلى الحالة. لا يحدث الرصيد هنا مباشرة لأن State يتحكم في القواعد والحالة.
     */
    public synchronized void deposit(Money amount) {
        logger.debug("deposit called: account={} amount={}", accountNumber, amount);
        state.deposit(this, amount);
        // notifyObservers is usually done by State or caller after successful operation
    }

    /**
     * Withdraw: يفوض إلى الحالة. لا يحدث الرصيد هنا مباشرة لأن State يتحكم في القواعد والحالة.
     */
    public synchronized void withdraw(Money amount) {
        logger.debug("withdraw called: account={} amount={}", accountNumber, amount);
        state.withdraw(this, amount);
        // notifyObservers is usually done by State or caller after successful operation
    }

    /**
     * Internal helper to apply balance changes from State implementations.
     * State implementations يجب أن تستدعي internalSetBalance عند تغيير الرصيد.
     */
    public synchronized void applyBalanceChange(Money newBalance) {
        this.balance = newBalance;
        logger.debug("applyBalanceChange account={} newBalance={}", accountNumber, balance);
    }

    public void notifyObservers(AccountEvent event) {
        for (Observer observer : observers) {
            try {
                observer.update(event);
            } catch (Exception e) {
                logger.warn("Observer failed for account {}: {}", accountNumber, e.getMessage(), e);
            }
        }
    }

    public void close() {
        state.close(this);
        logger.info("Account {} closed", accountNumber);
        notifyObservers(new AccountEvent("STATE_CHANGE", this, null, "Account closed"));
    }

    public synchronized void freeze() {
        state.freeze(this);
        logger.info("Account {} frozen. New state: {}", accountNumber, getStateDescription());
        notifyObservers(new AccountEvent("STATE_CHANGE", this, null, "Account frozen"));
    }

    public synchronized void suspend() {
        state.suspend(this);
        logger.info("Account {} suspended. New state: {}", accountNumber, getStateDescription());
        notifyObservers(new AccountEvent("STATE_CHANGE", this, null, "Account suspended"));
    }

    public synchronized void activate() {
        state.activate(this);
        logger.info("Account {} activated. New state: {}", accountNumber, getStateDescription());
        notifyObservers(new AccountEvent("STATE_CHANGE", this, null, "Account activated"));
    }

    public void getRecommendations() {
        logger.info("Recommendations for account {}: {}", accountNumber, recommendationEngine.generate(customer));
    }

    public Money calculateInterest() {
        return strategy.calculateInterest(balance);
    }

    public Money applyFees() {
        Money fees = strategy.applyFees(balance);
        // Ensure State/Account agreement: if fees applied here, make sure states don't reapply
        internalSetBalance(balance.subtract(fees));
        logger.info("Applied fees on account {}: fees={} newBalance={}", accountNumber, fees, balance);
        return fees;
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
        logger.debug("Observer added to account {}: {}", accountNumber, observer.getClass().getSimpleName());
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
        logger.debug("Observer removed from account {}: {}", accountNumber, observer.getClass().getSimpleName());
    }

    @Override
    public String toString() {
        return "Account[" + accountNumber + "] Balance: " + balance +
                " | State: " + getStateDescription() +
                " | Created: " + creationDate;
    }
}