package com.kowallo.accounts.budget.account.service;

import com.kowallo.accounts.budget.account.dto.AccountResponse;
import com.kowallo.accounts.budget.account.dto.CreateAccountRequest;
import com.kowallo.accounts.budget.account.mapper.AccountMapper;
import com.kowallo.accounts.budget.account.model.Account;
import com.kowallo.accounts.budget.account.repository.AccountRepository;
import com.kowallo.accounts.budget.common.exception.AccountAlreadyExistsException;
import com.kowallo.accounts.budget.common.exception.AccountHasTransactionsException;
import com.kowallo.accounts.budget.common.exception.AccountNotFoundException;
import com.kowallo.accounts.budget.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void createAccount_WhenNameIsUnique_ShouldSaveAndReturnResponse() {
        // given
        CreateAccountRequest request = new CreateAccountRequest("Savings");
        Account savedAccount = new Account("Savings");
        AccountResponse expectedResponse = new AccountResponse(UUID.randomUUID(), "Savings", BigDecimal.ZERO, LocalDateTime.now());
        
        when(accountRepository.existsByNameIgnoreCase("Savings")).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        when(accountMapper.toResponse(savedAccount)).thenReturn(expectedResponse);

        // when
        AccountResponse response = accountService.createAccount(request);

        // then
        assertThat(response).isEqualTo(expectedResponse);
        
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        assertThat(accountCaptor.getValue().getName()).isEqualTo("Savings");
    }

    @Test
    void createAccount_WithWhitespacesInName_ShouldTrimAndSave() {
        // given
        CreateAccountRequest request = new CreateAccountRequest("  Savings  ");
        Account savedAccount = new Account("Savings");
        AccountResponse expectedResponse = new AccountResponse(UUID.randomUUID(), "Savings", BigDecimal.ZERO, LocalDateTime.now());
        
        when(accountRepository.existsByNameIgnoreCase("Savings")).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        when(accountMapper.toResponse(savedAccount)).thenReturn(expectedResponse);

        // when
        AccountResponse response = accountService.createAccount(request);

        // then
        assertThat(response).isEqualTo(expectedResponse);
        
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        assertThat(accountCaptor.getValue().getName()).isEqualTo("Savings");
    }

    @Test
    void createAccount_WhenNameExists_ShouldThrowException() {
        // given
        CreateAccountRequest request = new CreateAccountRequest("Savings");
        when(accountRepository.existsByNameIgnoreCase("Savings")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> accountService.createAccount(request))
                .isInstanceOf(AccountAlreadyExistsException.class)
                .hasMessageContaining("Savings");
                
        verify(accountRepository, never()).save(any());
    }

    @Test
    void getAccount_WhenExists_ShouldReturnResponse() {
        // given
        UUID accountId = UUID.randomUUID();
        Account account = new Account("Savings");
        AccountResponse expectedResponse = new AccountResponse(accountId, "Savings", BigDecimal.ZERO, LocalDateTime.now());
        
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toResponse(account)).thenReturn(expectedResponse);

        // when
        AccountResponse response = accountService.getAccount(accountId);

        // then
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void getAccount_WhenNotExists_ShouldThrowException() {
        // given
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accountService.getAccount(accountId))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void getAllAccounts_ShouldReturnPageOfResponses() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Account account = new Account("Savings");
        Page<Account> accountPage = new PageImpl<>(List.of(account));
        Page<AccountResponse> expectedPage = new PageImpl<>(List.of(new AccountResponse(UUID.randomUUID(), "Savings", BigDecimal.ZERO, LocalDateTime.now())));
        
        when(accountRepository.findAll(pageable)).thenReturn(accountPage);
        when(accountMapper.toResponsePage(accountPage)).thenReturn(expectedPage);

        // when
        Page<AccountResponse> result = accountService.getAllAccounts(pageable);

        // then
        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    void deleteAccount_WhenExistsAndHasNoTransactions_ShouldDelete() {
        // given
        UUID accountId = UUID.randomUUID();
        Account account = new Account("Savings");
        
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.existsByAccountId(accountId)).thenReturn(false);

        // when
        accountService.deleteAccount(accountId);

        // then
        verify(accountRepository).delete(account);
    }

    @Test
    void deleteAccount_WhenHasTransactions_ShouldThrowException() {
        // given
        UUID accountId = UUID.randomUUID();
        Account account = new Account("Savings");
        
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.existsByAccountId(accountId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> accountService.deleteAccount(accountId))
                .isInstanceOf(AccountHasTransactionsException.class);
                
        verify(accountRepository, never()).delete(any());
    }
}
