package com.kowallo.accounts.budget.common.exception;

import java.util.UUID;

public class AccountHasBudgetsException extends ResourceConflictException {
    public AccountHasBudgetsException(UUID accountId) {
        super(String.format("Cannot delete account %s because it has existing budget limits", accountId), "ACCOUNT_HAS_BUDGETS");
    }
}
