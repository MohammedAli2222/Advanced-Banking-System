package com.bank.transactions;

import com.bank.core.Account;
import com.bank.utils.Money;
import com.bank.utils.Schedule;
import com.bank.utils.TransactionType;
import com.bank.utils.DateTime;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class RecurringTransaction extends Transaction {
    private Schedule schedule;
    private Timer timer;
    private Consumer<Transaction> executorCallback;

    // Constructor for single-account recurring transactions (deposit/withdraw/payment)
    public RecurringTransaction(String id, Account account, Money amount, TransactionType type, Schedule schedule, Consumer<Transaction> executorCallback) {
        super(id, account, amount, type);
        this.schedule = schedule;
        this.executorCallback = executorCallback;
        startReminder();
    }

    // Constructor for transfer recurring transactions (source + destination)
    public RecurringTransaction(String id, Account account, Account destinationAccount, Money amount, TransactionType type, Schedule schedule, Consumer<Transaction> executorCallback) {
        // use Transaction's new constructor supporting destination
        super(id, account, destinationAccount, amount, type);
        this.schedule = schedule;
        this.executorCallback = executorCallback;
        startReminder();
    }

    private void startReminder() {
        timer = new Timer(true);  // daemon thread so it doesn't prevent JVM exit

        long period = getPeriodInMillis();

        if (period <= 0) {
            System.out.println("[RECURRENCE] Invalid or unsupported schedule, reminder disabled for " + getTransactionId());
            return;
        }

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("[REMINDER] Recurring Transaction due now:");
                System.out.println("  ID: " + getTransactionId());
                System.out.println("  Type: " + getType());
                System.out.println("  Amount: " + getAmount());
                System.out.println("  Account: " + getAccount().getAccountNumber());
                if (getDestinationAccount() != null) {
                    System.out.println("  Destination: " + getDestinationAccount().getAccountNumber());
                }
                System.out.println("  Time: " + new DateTime());

                // Create a fresh Transaction object for this run (to have new timestamp/ID if desired)
                Transaction runTxn;
                if (getType() == TransactionType.TRANSFER) {
                    // For transfers, preserve source & destination
                    runTxn = new Transaction(getTransactionId() + "-" + System.currentTimeMillis(),
                            getAccount(), getDestinationAccount(), getAmount(), getType());
                } else {
                    runTxn = new Transaction(getTransactionId() + "-" + System.currentTimeMillis(),
                            getAccount(), getAmount(), getType());
                }

                // Execute via provided callback (e.g., DashboardFacade::processTransaction)
                try {
                    if (executorCallback != null) {
                        executorCallback.accept(runTxn);
                    } else {
                        System.out.println("[RECURRENCE] No executor callback provided for recurring transaction: " + getTransactionId());
                    }
                } catch (Exception e) {
                    System.out.println("[RECURRENCE] Exception while executing recurring transaction: " + e.getMessage());
                }
            }
        }, period, period);  // first execution after period, then every period
    }

    private long getPeriodInMillis() {
        if (schedule == Schedule.DAILY) {
            return 24L * 60 * 60 * 1000;  // 1 day
        } else if (schedule == Schedule.WEEKLY) {
            return 7L * 24 * 60 * 60 * 1000;
        } else if (schedule == Schedule.MONTHLY) {
            return 30L * 24 * 60 * 60 * 1000;  // approximate
        } else if (schedule == Schedule.YEARLY) {
            return 365L * 24 * 60 * 60 * 1000;
        } else {
            return 60 * 1000L;  // default: 1 minute for demo/testing
        }
    }

    public void cancel() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            System.out.println("[RECURRENCE] Cancelled recurring transaction: " + getTransactionId());
        }
    }
}