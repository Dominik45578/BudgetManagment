package com.kowallo.accounts.budget.budget.service;

import com.kowallo.accounts.budget.account.model.Account;
import com.kowallo.accounts.budget.account.repository.AccountRepository;
import com.kowallo.accounts.budget.budget.dto.BudgetResponse;
import com.kowallo.accounts.budget.budget.dto.CreateBudgetRequest;
import com.kowallo.accounts.budget.budget.mapper.BudgetMapper;
import com.kowallo.accounts.budget.budget.model.CategoryBudget;
import com.kowallo.accounts.budget.budget.repository.CategoryBudgetRepository;
import com.kowallo.accounts.budget.common.exception.AccountNotFoundException;
import com.kowallo.accounts.budget.common.exception.BudgetAlreadyExistsException;
import com.kowallo.accounts.budget.common.exception.BudgetNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final CategoryBudgetRepository budgetRepository;
    private final AccountRepository accountRepository;
    private final BudgetMapper budgetMapper;

    @Override
    @Transactional
    public BudgetResponse createBudget(CreateBudgetRequest request) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new AccountNotFoundException(request.accountId()));

        String trimmedCategory = request.category().trim();
        if (budgetRepository.findByAccountIdAndCategoryIgnoreCase(request.accountId(), trimmedCategory).isPresent()) {
            throw new BudgetAlreadyExistsException(request.accountId(), trimmedCategory);
        }

        CategoryBudget budget = new CategoryBudget(account, trimmedCategory, request.monthlyLimit());
        CategoryBudget savedBudget = budgetRepository.save(budget);

        return budgetMapper.toResponse(savedBudget);
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetResponse getBudget(UUID id) {
        CategoryBudget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new BudgetNotFoundException(id));
        return budgetMapper.toResponse(budget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgetsByAccountId(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }
        
        return budgetRepository.findAllByAccountId(accountId).stream()
                .map(budgetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBudget(UUID id) {
        CategoryBudget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new BudgetNotFoundException(id));
                
        budgetRepository.delete(budget);
    }
}
