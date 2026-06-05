package com.kowallo.accounts.budget.budget.service;

import com.kowallo.accounts.budget.budget.dto.BudgetResponse;
import com.kowallo.accounts.budget.budget.dto.CreateBudgetRequest;

import java.util.List;
import java.util.UUID;

public interface BudgetService {
    BudgetResponse createBudget(CreateBudgetRequest request);
    BudgetResponse getBudget(UUID id);
    List<BudgetResponse> getBudgetsByAccountId(UUID accountId);
    void deleteBudget(UUID id);
}
