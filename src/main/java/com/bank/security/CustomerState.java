package com.bank.security;

public class CustomerState extends AbstractUserState {

    public CustomerState() {
        permissions.add(Operation.CREATE_ACCOUNT);
        permissions.add(Operation.NORMAL_TRANSACTION);
        permissions.add(Operation.VIEW_REPORTS);
        // لا يسمح بالعمليات الحساسة
    }

    @Override
    public String getRoleName() {
        return "Customer";
    }
}
