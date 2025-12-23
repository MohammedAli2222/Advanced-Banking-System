package com.bank.transactions;

import com.bank.core.AccountComponent;
import com.bank.utils.*;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class RecurringTransaction extends Transaction {

    private Schedule schedule;
    private Timer timer;
    private Consumer<Transaction> executorCallback;

    public RecurringTransaction(String id,
                                AccountComponent account,
                                Money amount,
                                TransactionType type,
                                Schedule schedule,
                                Consumer<Transaction> executorCallback) {

        super(id, account, amount, type);
        this.schedule = schedule;
        this.executorCallback = executorCallback;
        startReminder();
    }

    public RecurringTransaction(String id,
                                AccountComponent account,
                                AccountComponent destinationAccount,
                                Money amount,
                                TransactionType type,
                                Schedule schedule,
                                Consumer<Transaction> executorCallback) {

        super(id, account, destinationAccount, amount, type);
        this.schedule = schedule;
        this.executorCallback = executorCallback;
        startReminder();
    }

    private void startReminder() {
        timer = new Timer(true);
        long period = getPeriodInMillis();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                Transaction runTxn;

                if (getType() == TransactionType.TRANSFER) {
                    runTxn = new Transaction(
                            getTransactionId() + "-" + System.currentTimeMillis(),
                            getAccount(),
                            getDestinationAccount(),
                            getAmount(),
                            getType()
                    );
                } else {
                    runTxn = new Transaction(
                            getTransactionId() + "-" + System.currentTimeMillis(),
                            getAccount(),
                            getAmount(),
                            getType()
                    );
                }

                executorCallback.accept(runTxn);
            }
        }, period, period);
    }

    private long getPeriodInMillis() {
        switch (schedule) {
            case DAILY: return 86_400_000L;
            case WEEKLY: return 604_800_000L;
            case MONTHLY: return 2_592_000_000L;
            case YEARLY: return 31_536_000_000L;
            default: return 60_000L;
        }
    }
}
