package com.kowallo.accounts.budget.account.service;

import com.kowallo.accounts.budget.account.dto.AccountResponse;
import com.kowallo.accounts.budget.account.dto.CreateAccountRequest;
import com.kowallo.accounts.budget.account.mapper.AccountMapper;
import com.kowallo.accounts.budget.account.model.Account;
import com.kowallo.accounts.budget.account.repository.AccountRepository;
import com.kowallo.accounts.budget.budget.repository.CategoryBudgetRepository;
import com.kowallo.accounts.budget.common.exception.AccountAlreadyExistsException;
import com.kowallo.accounts.budget.common.exception.AccountHasBudgetsException;
import com.kowallo.accounts.budget.common.exception.AccountHasTransactionsException;
import com.kowallo.accounts.budget.common.exception.AccountNotFoundException;
import com.kowallo.accounts.budget.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryBudgetRepository categoryBudgetRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        String trimmedName = request.name().trim();
        if (accountRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new AccountAlreadyExistsException(trimmedName);
        }

        Account account = new Account(trimmedName);
        Account savedAccount = accountRepository.save(account);
        
        return accountMapper.toResponse(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccount(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
                
        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountResponse> getAllAccounts(Pageable pageable) {
        return accountMapper.toResponsePage(accountRepository.findAll(pageable));
    }

    @Override
    @Transactional
    public void deleteAccount(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
                
        if (transactionRepository.existsByAccountId(id)) {
            throw new AccountHasTransactionsException(id);
        }

        if (categoryBudgetRepository.existsByAccountId(id)) {
            throw new AccountHasBudgetsException(id);
        }
        
        accountRepository.delete(account);
    }
}
