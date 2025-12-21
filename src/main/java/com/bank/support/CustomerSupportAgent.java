package com.bank.support;

public class CustomerSupportAgent extends AbstractSupportHandler {

    @Override
    protected boolean canHandle(Ticket ticket) {
        // يحل مشاكل بسيطة (مثل استفسار رصيد أو معلومات عامة)
        return ticket.getDescription().toLowerCase().contains("balance") ||
                ticket.getDescription().toLowerCase().contains("info");
    }

    @Override
    protected void process(Ticket ticket) {
        System.out.println("Customer Support Agent handling simple inquiry.");
    }

    @Override
    protected String getHandlerName() {
        return "Customer Support Agent";
    }
}