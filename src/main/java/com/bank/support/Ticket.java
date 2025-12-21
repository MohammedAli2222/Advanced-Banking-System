package com.bank.support;

import com.bank.utils.DateTime;

public class Ticket {
    private String ticketId;
    private String description;
    private String customerId;
    private String status;  // OPEN, IN_PROGRESS, RESOLVED, ESCALATED
    private DateTime creationDate;

    public Ticket(String ticketId, String customerId, String description) {
        this.ticketId = ticketId;
        this.customerId = customerId;
        this.description = description;
        this.status = "OPEN";
        this.creationDate = new DateTime();
    }

    // Getters & Setters
    public String getTicketId() { return ticketId; }
    public String getDescription() { return description; }
    public String getCustomerId() { return customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public DateTime getCreationDate() { return creationDate; }

    @Override
    public String toString() {
        return "Ticket[" + ticketId + "] Customer: " + customerId +
                " | Description: " + description +
                " | Status: " + status +
                " | Created: " + creationDate;
    }
}