package com.bank.transactions;

import com.bank.core.Account;
import com.bank.utils.Money;
import com.bank.utils.TransactionType;
import com.bank.utils.TransactionStatus;
import com.bank.utils.DateTime;

import java.util.UUID;

public class Transaction {
    private String transactionId;
    private Account account;               // source account
    private Account destinationAccount;    // null unless TRANSFER
    private Money amount;
    private TransactionType type;
    private TransactionStatus status;
    private DateTime timestamp;

    // Existing constructor (keeps backward compatibility) - no destination
    public Transaction(String transactionIdPrefix, Account account, Money amount, TransactionType type) {
        this(transactionIdPrefix + "-" + UUID.randomUUID().toString(), account, null, amount, type);
    }

    // New constructor supporting destination account (for TRANSFER)
    public Transaction(String transactionId, Account account, Account destinationAccount, Money amount, TransactionType type) {
        this.transactionId = transactionId;
        this.account = account;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.type = type;
        this.status = TransactionStatus.PENDING;
        this.timestamp = new DateTime();
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public Account getAccount() { return account; }  // source
    public Account getDestinationAccount() { return destinationAccount; } // may be null
    public Money getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public TransactionStatus getStatus() { return status; }
    public DateTime getTimestamp() { return timestamp; }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Transaction[").append(transactionId).append("] Type: ").append(type)
                .append(" | Amount: ").append(amount)
                .append(" | Status: ").append(status)
                .append(" | Time: ").append(timestamp);
        if (destinationAccount != null) {
            sb.append(" | From: ").append(account.getAccountNumber())
                    .append(" | To: ").append(destinationAccount.getAccountNumber());
        } else {
            sb.append(" | Account: ").append(account.getAccountNumber());
        }
        return sb.toString();
    }
}