package com.bank.security;

public class AdminState implements UserState {
    @Override
    public boolean canCreateAccount() { return true; }

    @Override
    public boolean canProcessNormalTransaction() { return true; }

    @Override
    public boolean canProcessLargeTransaction() { return true; }

    @Override
    public boolean canFreezeAccount() { return true; }

    @Override
    public boolean canSuspendAccount() { return true; }

    @Override
    public boolean canCloseAccount() { return true; }

    @Override
    public boolean canViewReports() { return true; }

    @Override
    public boolean canHandleSupportTicket() { return true; }

    @Override
    public boolean canApproveTransaction() { return true; }

    @Override
    public String getRoleName() { return "Admin"; }
}