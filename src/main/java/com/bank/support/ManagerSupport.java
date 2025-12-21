package com.bank.support;

public class ManagerSupport extends AbstractSupportHandler {

    @Override
    protected boolean canHandle(Ticket ticket) {
        return ticket.getDescription().toLowerCase().contains("limit") ||
                ticket.getDescription().toLowerCase().contains("fee") ||
                ticket.getDescription().toLowerCase().contains("dispute");
    }

    @Override
    protected void process(Ticket ticket) {
        System.out.println("Manager reviewing financial dispute or limit issue.");
    }

    @Override
    protected String getHandlerName() {
        return "Manager Support";
    }
}