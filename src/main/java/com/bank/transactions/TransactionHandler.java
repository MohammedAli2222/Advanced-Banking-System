package com.bank.transactions;

public interface TransactionHandler {
    void setNextHandler(TransactionHandler nextHandler);
    void handle(Transaction transaction);
}