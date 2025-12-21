package com.bank.transactions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TransactionExecutor {
    private final ExecutorService pool;
    private final TransactionHandler transactionChain;

    public TransactionExecutor(TransactionHandler transactionChain, int threadPoolSize) {
        this.transactionChain = transactionChain;
        this.pool = Executors.newFixedThreadPool(threadPoolSize);
    }

    /**
     * Submit a transaction for asynchronous processing.
     * Returns a Future that completes when processing finishes.
     */
    public Future<Void> submit(Transaction transaction) {
        return pool.submit(() -> {
            transactionChain.handle(transaction);
            return null;
        });
    }

    /**
     * Submit many transactions and wait for them to finish (with timeout).
     */
    public List<Future<Void>> submitAll(List<Transaction> transactions) {
        List<Future<Void>> futures = new ArrayList<>();
        for (Transaction t : transactions) {
            futures.add(submit(t));
        }
        return futures;
    }

    public void shutdownGracefully(long timeout, TimeUnit unit) throws InterruptedException {
        pool.shutdown();
        if (!pool.awaitTermination(timeout, unit)) {
            pool.shutdownNow();
        }
    }
}   