package com.kowallo.accounts.budget.transaction.service;

import com.kowallo.accounts.budget.account.model.Account;
import com.kowallo.accounts.budget.account.repository.AccountRepository;
import com.kowallo.accounts.budget.budget.model.CategoryBudget;
import com.kowallo.accounts.budget.budget.repository.CategoryBudgetRepository;
import com.kowallo.accounts.budget.common.exception.AccountNotFoundException;
import com.kowallo.accounts.budget.common.exception.TransactionNotFoundException;
import com.kowallo.accounts.budget.transaction.dto.CreateTransactionRequest;
import com.kowallo.accounts.budget.transaction.dto.TransactionResponse;
import com.kowallo.accounts.budget.transaction.mapper.TransactionMapper;
import com.kowallo.accounts.budget.transaction.model.Transaction;
import com.kowallo.accounts.budget.transaction.model.TransactionType;
import com.kowallo.accounts.budget.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryBudgetRepository categoryBudgetRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void createTransaction_WhenAccountNotFound_ShouldThrowException() {
        // given
        CreateTransactionRequest request = new CreateTransactionRequest(UUID.randomUUID(), BigDecimal.TEN, TransactionType.EXPENSE, "Food", "Lunch", LocalDateTime.now());
        when(accountRepository.findByIdForUpdate(request.accountId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void createTransaction_WithExpenseExceedingBudget_ShouldReturnWarning() {
        // given
        UUID accountId = UUID.randomUUID();
        CreateTransactionRequest request = new CreateTransactionRequest(accountId, BigDecimal.valueOf(100), TransactionType.EXPENSE, "Food", "Lunch", LocalDateTime.now());
        Account account = new Account("Savings");
        CategoryBudget budget = new CategoryBudget(account, "Food", BigDecimal.valueOf(150));
        Transaction savedTransaction = Transaction.builder().account(account).amount(request.amount()).type(request.type()).category(request.category()).transactionDate(request.transactionDate()).build();
        TransactionResponse responseWithWarning = new TransactionResponse(UUID.randomUUID(), accountId, "Savings", BigDecimal.valueOf(100), TransactionType.EXPENSE, "Food", "Lunch", request.transactionDate(), LocalDateTime.now(), "Budget limit exceeded");

        when(accountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(account));
        when(categoryBudgetRepository.findByAccountIdAndCategoryIgnoreCase(accountId, "Food")).thenReturn(Optional.of(budget));
        when(transactionRepository.sumExpensesByAccountAndCategoryInDateRange(eq(accountId), eq("Food"), any(), any())).thenReturn(BigDecimal.valueOf(100));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(transactionMapper.toResponse(eq(savedTransaction), contains("Budget limit exceeded"))).thenReturn(responseWithWarning);

        // when
        TransactionResponse response = transactionService.createTransaction(request);

        // then
        assertThat(response).isEqualTo(responseWithWarning);
        assertThat(account.getBalance()).isEqualTo(BigDecimal.valueOf(-100)); // EXPENSE applies -100
        verify(accountRepository).save(account);
    }

    @Test
    void createTransaction_WhenIncome_ShouldUpdateBalanceAndReturnResponse() {
        // given
        UUID accountId = UUID.randomUUID();
        CreateTransactionRequest request = new CreateTransactionRequest(accountId, BigDecimal.valueOf(200), TransactionType.INCOME, "Salary", "Bonus", LocalDateTime.now());
        Account account = new Account("Savings");
        account.applyTransaction(TransactionType.INCOME, BigDecimal.valueOf(100)); // previous balance 100
        
        Transaction savedTransaction = Transaction.builder().account(account).amount(request.amount()).type(request.type()).category(request.category()).transactionDate(request.transactionDate()).build();
        TransactionResponse expectedResponse = new TransactionResponse(UUID.randomUUID(), accountId, "Savings", BigDecimal.valueOf(200), TransactionType.INCOME, "Salary", "Bonus", request.transactionDate(), LocalDateTime.now(), null);

        when(accountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(transactionMapper.toResponse(eq(savedTransaction), isNull())).thenReturn(expectedResponse);

        // when
        TransactionResponse response = transactionService.createTransaction(request);

        // then
        assertThat(response).isEqualTo(expectedResponse);
        assertThat(account.getBalance()).isEqualTo(BigDecimal.valueOf(300));
        verify(accountRepository).save(account);
    }

    @Test
    void createTransaction_WithExpenseNotExceedingBudget_ShouldReturnWithoutWarning() {
        // given
        UUID accountId = UUID.randomUUID();
        CreateTransactionRequest request = new CreateTransactionRequest(accountId, BigDecimal.valueOf(100), TransactionType.EXPENSE, "Food", "Lunch", LocalDateTime.now());
        Account account = new Account("Savings");
        CategoryBudget budget = new CategoryBudget(account, "Food", BigDecimal.valueOf(500));
        Transaction savedTransaction = Transaction.builder().account(account).amount(request.amount()).type(request.type()).category(request.category()).transactionDate(request.transactionDate()).build();
        TransactionResponse expectedResponse = new TransactionResponse(UUID.randomUUID(), accountId, "Savings", BigDecimal.valueOf(100), TransactionType.EXPENSE, "Food", "Lunch", request.transactionDate(), LocalDateTime.now(), null);

        when(accountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(account));
        when(categoryBudgetRepository.findByAccountIdAndCategoryIgnoreCase(accountId, "Food")).thenReturn(Optional.of(budget));
        when(transactionRepository.sumExpensesByAccountAndCategoryInDateRange(eq(accountId), eq("Food"), any(), any())).thenReturn(BigDecimal.valueOf(300));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(transactionMapper.toResponse(eq(savedTransaction), isNull())).thenReturn(expectedResponse);

        // when
        TransactionResponse response = transactionService.createTransaction(request);

        // then
        assertThat(response).isEqualTo(expectedResponse);
        verify(accountRepository).save(account);
    }

    @Test
    void getTransaction_WhenNotFound_ShouldThrowException() {
        // given
        UUID id = UUID.randomUUID();
        when(transactionRepository.findByIdWithAccount(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> transactionService.getTransaction(id))
                .isInstanceOf(TransactionNotFoundException.class);
    }

    @Test
    void getTransactions_ShouldReturnPage() {
        // given
        Specification<Transaction> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        Account account = new Account("Savings");
        Transaction transaction = Transaction.builder().account(account).amount(BigDecimal.TEN).type(TransactionType.INCOME).category("Salary").transactionDate(LocalDateTime.now()).build();
        Page<Transaction> page = new PageImpl<>(List.of(transaction));
        TransactionResponse response = new TransactionResponse(UUID.randomUUID(), UUID.randomUUID(), "Savings", BigDecimal.TEN, TransactionType.INCOME, "Salary", null, LocalDateTime.now(), LocalDateTime.now(), null);
        
        when(transactionRepository.findAll(spec, pageable)).thenReturn(page);
        when(transactionRepository.findAllWithAccountByIds(any(), eq(pageable))).thenReturn(page);
        when(transactionMapper.toResponse(transaction)).thenReturn(response);

        // when
        Page<TransactionResponse> result = transactionService.getTransactions(spec, pageable);

        // then
        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    void getTransactions_WhenNoIdsFound_ShouldReturnEmptyPage() {
        // given
        Specification<Transaction> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(transactionRepository.findAll(spec, pageable)).thenReturn(Page.empty(pageable));

        // when
        Page<TransactionResponse> result = transactionService.getTransactions(spec, pageable);

        // then
        assertThat(result).isEmpty();
        verify(transactionRepository, never()).findAllWithAccountByIds(any(), any());
    }
}
