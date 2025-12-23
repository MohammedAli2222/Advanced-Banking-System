package com.bank.security;

public class AdminState extends AbstractUserState {

    public AdminState() {
        permissions.add(Operation.CREATE_ACCOUNT);
        permissions.add(Operation.NORMAL_TRANSACTION);
        permissions.add(Operation.LARGE_TRANSACTION);
        permissions.add(Operation.FREEZE_ACCOUNT);
        permissions.add(Operation.SUSPEND_ACCOUNT);
        permissions.add(Operation.CLOSE_ACCOUNT);
        permissions.add(Operation.VIEW_REPORTS);
        permissions.add(Operation.HANDLE_TICKET);
        permissions.add(Operation.APPROVE_TRANSACTION);
    }

    @Override
    public String getRoleName() {
        return "Admin";
    }
}
