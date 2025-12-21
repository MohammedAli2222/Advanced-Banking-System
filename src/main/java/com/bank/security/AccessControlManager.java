package com.bank.security;

public class AccessControlManager {
    private UserState currentState;

    public AccessControlManager(UserState initialState) {
        this.currentState = initialState;
    }

    public boolean isAllowed(String operation) {
        if ("createAccount".equals(operation)) {
            return currentState.canCreateAccount();
        } else if ("normalTransaction".equals(operation)) {
            return currentState.canProcessNormalTransaction();
        } else if ("largeTransaction".equals(operation)) {
            return currentState.canProcessLargeTransaction();
        } else if ("freezeAccount".equals(operation)) {
            return currentState.canFreezeAccount();
        } else if ("suspendAccount".equals(operation)) {
            return currentState.canSuspendAccount();
        } else if ("closeAccount".equals(operation)) {
            return currentState.canCloseAccount();
        } else if ("viewReports".equals(operation)) {
            return currentState.canViewReports();
        } else if ("handleTicket".equals(operation)) {
            return currentState.canHandleSupportTicket();
        } else if ("approveTransaction".equals(operation)) {
            return currentState.canApproveTransaction();
        } else {
            return false;
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