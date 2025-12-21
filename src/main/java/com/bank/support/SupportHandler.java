package com.bank.support;

public interface SupportHandler {
    void setNextHandler(SupportHandler nextHandler);
    void handleTicket(Ticket ticket);
}