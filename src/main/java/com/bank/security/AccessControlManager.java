package com.bank.security;

public class AccessControlManager {

    private UserState currentState;

    public AccessControlManager(UserState initialState) {
        this.currentState = initialState;
    }

    public boolean isAllowed(Operation op) {
        switch (op) {
            case CREATE_ACCOUNT: return currentState.canCreateAccount();
            case NORMAL_TRANSACTION: return currentState.canProcessNormalTransaction();
            case LARGE_TRANSACTION: return currentState.canProcessLargeTransaction();
            case FREEZE_ACCOUNT: return currentState.canFreezeAccount();
            case SUSPEND_ACCOUNT: return currentState.canSuspendAccount();
            case CLOSE_ACCOUNT: return currentState.canCloseAccount();
            case VIEW_REPORTS: return currentState.canViewReports();
            case HANDLE_TICKET: return currentState.canHandleSupportTicket();
            case APPROVE_TRANSACTION: return currentState.canApproveTransaction();
            case INTERNATIONAL_TRANSFER: return ((AbstractUserState)currentState).canProcessInternationalTransfer();
            default: return false;
        }
    }

    public String getCurrentRole() {
        return currentState.getRoleName();
    }

    public void setState(UserState newState) {
        this.currentState = newState;
        System.out.println("User role changed to: " + newState.getRoleName());
    }
}
