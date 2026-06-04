package com.kowallo.accounts.budget.common.exception;

public class AccountAlreadyExistsException extends ResourceConflictException {
    public AccountAlreadyExistsException(String accountName) {
        super(String.format("Account with name '%s' already exists", accountName), "ACCOUNT_ALREADY_EXISTS");
    }
}
