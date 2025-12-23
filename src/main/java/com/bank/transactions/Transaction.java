package com.bank.transactions;

import com.bank.core.AccountComponent;
import com.bank.utils.*;

import java.util.UUID;

public class Transaction {

    private String transactionId;
    private AccountComponent account;
    private AccountComponent destinationAccount;
    private Money amount;
    private TransactionType type;
    private TransactionStatus status;
    private DateTime timestamp;

    public Transaction(String prefix, AccountComponent account, Money amount, TransactionType type) {
        this(prefix + "-" + UUID.randomUUID(), account, null, amount, type);
    }

    public Transaction(String id,
                       AccountComponent account,
                       AccountComponent destinationAccount,
                       Money amount,
                       TransactionType type) {

        this.transactionId = id;
        this.account = account;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.type = type;
        this.status = TransactionStatus.PENDING;
        this.timestamp = new DateTime();
    }

    // ========= Getters =========
    public String getTransactionId() { return transactionId; }
    public AccountComponent getAccount() { return account; }
    public AccountComponent getDestinationAccount() { return destinationAccount; }
    public Money getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public TransactionStatus getStatus() { return status; }
    public DateTime getTimestamp() { return timestamp; }

    // ✅ كان الخطأ هنا → أضفناه
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}
