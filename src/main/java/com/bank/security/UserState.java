package com.bank.security;

public interface UserState {
    boolean canCreateAccount();
    boolean canProcessNormalTransaction();
    boolean canProcessLargeTransaction();
    boolean canFreezeAccount();
    boolean canSuspendAccount();
    boolean canCloseAccount();
    boolean canViewReports();
    boolean canHandleSupportTicket();
    boolean canApproveTransaction();
    String getRoleName();
}