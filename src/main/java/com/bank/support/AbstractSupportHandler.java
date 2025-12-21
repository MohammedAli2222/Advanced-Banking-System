package com.bank.support;

public abstract class AbstractSupportHandler implements SupportHandler {
    protected SupportHandler nextHandler;

    @Override
    public void setNextHandler(SupportHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handleTicket(Ticket ticket) {
        if (canHandle(ticket)) {
            process(ticket);
            ticket.setStatus("RESOLVED");
            System.out.println("Ticket resolved by " + getHandlerName() + ": " + ticket);
        } else if (nextHandler != null) {
            ticket.setStatus("ESCALATED");
            System.out.println("Ticket escalated to next level by " + getHandlerName());
            nextHandler.handleTicket(ticket);
        } else {
            ticket.setStatus("UNRESOLVED");
            System.out.println("Ticket could not be resolved at any level: " + ticket);
        }
    }

    protected abstract boolean canHandle(Ticket ticket);
    protected abstract void process(Ticket ticket);
    protected abstract String getHandlerName();
}