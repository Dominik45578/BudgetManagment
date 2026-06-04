package com.kowallo.accounts.budget.account.service;

import com.kowallo.accounts.budget.account.dto.AccountResponse;
import com.kowallo.accounts.budget.account.dto.CreateAccountRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AccountService {
    
    AccountResponse createAccount(CreateAccountRequest request);
    
    AccountResponse getAccount(UUID id);
    
    Page<AccountResponse> getAllAccounts(Pageable pageable);
    
    void deleteAccount(UUID id);
}
