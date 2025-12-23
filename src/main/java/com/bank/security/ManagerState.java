package com.bank.security;

public class ManagerState extends AbstractUserState {

    public ManagerState() {
        permissions.add(Operation.CREATE_ACCOUNT);
        permissions.add(Operation.NORMAL_TRANSACTION);
        permissions.add(Operation.LARGE_TRANSACTION);
        permissions.add(Operation.FREEZE_ACCOUNT);
        permissions.add(Operation.SUSPEND_ACCOUNT);
        permissions.add(Operation.VIEW_REPORTS);
        permissions.add(Operation.HANDLE_TICKET);
        permissions.add(Operation.APPROVE_TRANSACTION);
        // أضف أي صلاحية جديدة هنا
        permissions.add(Operation.INTERNATIONAL_TRANSFER);
    }

    @Override
    public String getRoleName() {
        return "Manager";
    }
}
