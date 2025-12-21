package com.bank.security;

public class TellerState implements UserState {
    @Override
    public boolean canCreateAccount() { return true; }

    @Override
    public boolean canProcessNormalTransaction() { return true; }

    @Override
    public boolean canProcessLargeTransaction() { return false; }

    @Override
    public boolean canFreezeAccount() { return false; }

    @Override
    public boolean canSuspendAccount() { return false; }

    @Override
    public boolean canCloseAccount() { return false; }

    @Override
    public boolean canViewReports() { return true; }

    @Override
    public boolean canHandleSupportTicket() { return true; }  // Teller يحل تذاكر عادية

    @Override
    public boolean canApproveTransaction() { return false; }

    @Override
    public String getRoleName() { return "Teller"; }
}