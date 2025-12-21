package com.bank.support;

public class AdminSupport extends AbstractSupportHandler {

    @Override
    protected boolean canHandle(Ticket ticket) {
        return true;  // Admin يحل كل حاجة
    }

    @Override
    protected void process(Ticket ticket) {
        System.out.println("Admin handling critical or system-level issue.");
    }

    @Override
    protected String getHandlerName() {
        return "Admin Support";
    }
}