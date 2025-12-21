package com.bank.support;

public class TellerSupport extends AbstractSupportHandler {

    @Override
    protected boolean canHandle(Ticket ticket) {
        return ticket.getDescription().toLowerCase().contains("deposit") ||
                ticket.getDescription().toLowerCase().contains("withdraw") ||
                ticket.getDescription().toLowerCase().contains("transfer");
    }

    @Override
    protected void process(Ticket ticket) {
        System.out.println("Teller assisting with daily transaction issue.");
    }

    @Override
    protected String getHandlerName() {
        return "Teller Support";
    }
}