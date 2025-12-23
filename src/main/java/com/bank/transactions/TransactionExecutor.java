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

    public Future<Void> submit(Transaction transaction) {
        return pool.submit(() -> {
            try {
                transactionChain.handle(transaction);
            } catch (Exception e) {
                // إعادة الرمي لتصل إلى الـ Facade
                throw e;
            }
            return null;
        });
    }

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
