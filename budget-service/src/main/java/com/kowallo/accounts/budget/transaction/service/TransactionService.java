package com.kowallo.accounts.budget.transaction.service;

import com.kowallo.accounts.budget.transaction.dto.CreateTransactionRequest;
import com.kowallo.accounts.budget.transaction.dto.TransactionResponse;
import com.kowallo.accounts.budget.transaction.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface TransactionService {
    
    TransactionResponse createTransaction(CreateTransactionRequest request);
    
    TransactionResponse getTransaction(UUID id);
    
    Page<TransactionResponse> getTransactions(Specification<Transaction> spec, Pageable pageable);
}
