package com.kowallo.accounts.budget.common.exception;

import java.util.UUID;

public class AccountHasTransactionsException extends ResourceConflictException {
    public AccountHasTransactionsException(UUID accountId) {
        super(String.format("Cannot delete account %s because it has existing transactions", accountId), "ACCOUNT_HAS_TRANSACTIONS");
    }
}
