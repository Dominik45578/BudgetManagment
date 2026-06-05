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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {

    @Mock
    private CategoryBudgetRepository budgetRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BudgetMapper budgetMapper;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    @Test
    void createBudget_WhenValid_ShouldSaveAndReturnResponse() {
        // given
        UUID accountId = UUID.randomUUID();
        CreateBudgetRequest request = new CreateBudgetRequest(accountId, "Groceries", BigDecimal.valueOf(500));
        Account account = new Account("Savings");
        CategoryBudget savedBudget = new CategoryBudget(account, "Groceries", BigDecimal.valueOf(500));
        BudgetResponse expectedResponse = new BudgetResponse(UUID.randomUUID(), accountId, "Savings", "Groceries", BigDecimal.valueOf(500), LocalDateTime.now());

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(budgetRepository.findByAccountIdAndCategoryIgnoreCase(accountId, "Groceries")).thenReturn(Optional.empty());
        when(budgetRepository.save(any(CategoryBudget.class))).thenReturn(savedBudget);
        when(budgetMapper.toResponse(savedBudget)).thenReturn(expectedResponse);

        // when
        BudgetResponse response = budgetService.createBudget(request);

        // then
        assertThat(response).isEqualTo(expectedResponse);
        verify(budgetRepository).save(any(CategoryBudget.class));
    }

    @Test
    void createBudget_WhenAccountNotFound_ShouldThrowException() {
        // given
        UUID accountId = UUID.randomUUID();
        CreateBudgetRequest request = new CreateBudgetRequest(accountId, "Groceries", BigDecimal.valueOf(500));
        
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> budgetService.createBudget(request))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void createBudget_WhenBudgetAlreadyExists_ShouldThrowException() {
        // given
        UUID accountId = UUID.randomUUID();
        CreateBudgetRequest request = new CreateBudgetRequest(accountId, "Groceries", BigDecimal.valueOf(500));
        Account account = new Account("Savings");
        CategoryBudget existingBudget = new CategoryBudget(account, "Groceries", BigDecimal.valueOf(300));
        
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(budgetRepository.findByAccountIdAndCategoryIgnoreCase(accountId, "Groceries")).thenReturn(Optional.of(existingBudget));

        // when & then
        assertThatThrownBy(() -> budgetService.createBudget(request))
                .isInstanceOf(BudgetAlreadyExistsException.class);
    }

    @Test
    void getBudget_WhenExists_ShouldReturnResponse() {
        // given
        UUID budgetId = UUID.randomUUID();
        CategoryBudget budget = new CategoryBudget(new Account("Savings"), "Groceries", BigDecimal.valueOf(500));
        BudgetResponse expectedResponse = new BudgetResponse(budgetId, UUID.randomUUID(), "Savings", "Groceries", BigDecimal.valueOf(500), LocalDateTime.now());
        
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(budgetMapper.toResponse(budget)).thenReturn(expectedResponse);

        // when
        BudgetResponse response = budgetService.getBudget(budgetId);

        // then
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void getBudget_WhenNotExists_ShouldThrowException() {
        // given
        UUID budgetId = UUID.randomUUID();
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> budgetService.getBudget(budgetId))
                .isInstanceOf(BudgetNotFoundException.class);
    }

    @Test
    void getBudgetsByAccountId_WhenExists_ShouldReturnList() {
        // given
        UUID accountId = UUID.randomUUID();
        CategoryBudget budget = new CategoryBudget(new Account("Savings"), "Groceries", BigDecimal.valueOf(500));
        BudgetResponse expectedResponse = new BudgetResponse(UUID.randomUUID(), accountId, "Savings", "Groceries", BigDecimal.valueOf(500), LocalDateTime.now());
        
        when(accountRepository.existsById(accountId)).thenReturn(true);
        when(budgetRepository.findAllByAccountId(accountId)).thenReturn(List.of(budget));
        when(budgetMapper.toResponse(budget)).thenReturn(expectedResponse);

        // when
        List<BudgetResponse> responses = budgetService.getBudgetsByAccountId(accountId);

        // then
        assertThat(responses).containsExactly(expectedResponse);
    }

    @Test
    void deleteBudget_WhenExists_ShouldDelete() {
        // given
        UUID budgetId = UUID.randomUUID();
        CategoryBudget budget = new CategoryBudget(new Account("Savings"), "Groceries", BigDecimal.valueOf(500));
        
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        // when
        budgetService.deleteBudget(budgetId);

        // then
        verify(budgetRepository).delete(budget);
    }
}
