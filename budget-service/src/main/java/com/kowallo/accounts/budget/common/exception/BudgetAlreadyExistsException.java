package com.kowallo.accounts.budget.common.exception;

import java.util.UUID;

public class BudgetAlreadyExistsException extends ResourceConflictException {
    public BudgetAlreadyExistsException(UUID accountId, String category) {
        super(String.format("Budget for account '%s' and category '%s' already exists", accountId, category), "BUDGET_ALREADY_EXISTS");
    }
}
