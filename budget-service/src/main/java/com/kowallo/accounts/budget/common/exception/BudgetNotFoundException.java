package com.kowallo.accounts.budget.common.exception;

import java.util.UUID;

public class BudgetNotFoundException extends ResourceNotFoundException {
    public BudgetNotFoundException(UUID budgetId) {
        super("CategoryBudget", budgetId);
    }
}
