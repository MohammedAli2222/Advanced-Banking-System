package com.bank.security;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractUserState implements UserState {

    protected Set<Operation> permissions = new HashSet<>();

    @Override
    public boolean canCreateAccount() {
        return permissions.contains(Operation.CREATE_ACCOUNT);
    }

    @Override
    public boolean canProcessNormalTransaction() {
        return permissions.contains(Operation.NORMAL_TRANSACTION);
    }

    @Override
    public boolean canProcessLargeTransaction() {
        return permissions.contains(Operation.LARGE_TRANSACTION);
    }

    @Override
    public boolean canFreezeAccount() {
        return permissions.contains(Operation.FREEZE_ACCOUNT);
    }

    @Override
    public boolean canSuspendAccount() {
        return permissions.contains(Operation.SUSPEND_ACCOUNT);
    }

    @Override
    public boolean canCloseAccount() {
        return permissions.contains(Operation.CLOSE_ACCOUNT);
    }

    @Override
    public boolean canViewReports() {
        return permissions.contains(Operation.VIEW_REPORTS);
    }

    @Override
    public boolean canHandleSupportTicket() {
        return permissions.contains(Operation.HANDLE_TICKET);
    }

    @Override
    public boolean canApproveTransaction() {
        return permissions.contains(Operation.APPROVE_TRANSACTION);
    }

    // مثال على عملية جديدة
    public boolean canProcessInternationalTransfer() {
        return permissions.contains(Operation.INTERNATIONAL_TRANSFER);
    }
}
