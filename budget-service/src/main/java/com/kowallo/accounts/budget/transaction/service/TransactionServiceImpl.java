package com.kowallo.accounts.budget.transaction.service;

import com.kowallo.accounts.budget.account.model.Account;
import com.kowallo.accounts.budget.account.repository.AccountRepository;
import com.kowallo.accounts.budget.budget.repository.CategoryBudgetRepository;
import com.kowallo.accounts.budget.common.exception.AccountNotFoundException;
import com.kowallo.accounts.budget.common.exception.TransactionNotFoundException;
import com.kowallo.accounts.budget.transaction.dto.CreateTransactionRequest;
import com.kowallo.accounts.budget.transaction.dto.TransactionResponse;
import com.kowallo.accounts.budget.transaction.mapper.TransactionMapper;
import com.kowallo.accounts.budget.transaction.model.Transaction;
import com.kowallo.accounts.budget.transaction.model.TransactionType;
import com.kowallo.accounts.budget.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryBudgetRepository categoryBudgetRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        Account account = accountRepository.findByIdForUpdate(request.accountId())
                .orElseThrow(() -> new AccountNotFoundException(request.accountId()));

        String trimmedCategory = request.category().trim();
        String trimmedDescription = request.description() != null ? request.description().trim() : null;

        CreateTransactionRequest cleanRequest = new CreateTransactionRequest(
                request.accountId(),
                request.amount(),
                request.type(),
                trimmedCategory,
                trimmedDescription,
                request.transactionDate()
        );

        String budgetWarning = null;
        if (cleanRequest.type() == TransactionType.EXPENSE) {
            budgetWarning = checkBudgetWarning(cleanRequest);
        }

        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(cleanRequest.amount())
                .type(cleanRequest.type())
                .category(cleanRequest.category())
                .description(cleanRequest.description())
                .transactionDate(cleanRequest.transactionDate())
                .build();

        account.applyTransaction(cleanRequest.type(), cleanRequest.amount());

        Transaction savedTransaction = transactionRepository.save(transaction);
        accountRepository.save(account);

        return transactionMapper.toResponse(savedTransaction, budgetWarning);
    }

    private String checkBudgetWarning(CreateTransactionRequest request) {
        return categoryBudgetRepository.findByAccountIdAndCategoryIgnoreCase(request.accountId(), request.category())
                .flatMap(budget -> {
                    YearMonth ym = YearMonth.from(request.transactionDate());
                    LocalDateTime startOfMonth = ym.atDay(1).atStartOfDay();
                    LocalDateTime endOfMonth = ym.plusMonths(1).atDay(1).atStartOfDay();
                    
                    BigDecimal previousExpenses = transactionRepository.sumExpensesByAccountAndCategoryInDateRange(
                            request.accountId(), request.category(), startOfMonth, endOfMonth);
                            
                    BigDecimal newTotal = previousExpenses.add(request.amount());
                    if (newTotal.compareTo(budget.getMonthlyLimit()) > 0) {
                        return java.util.Optional.of(String.format("Budget limit exceeded for %s: %s / %s", 
                                request.category(), newTotal, budget.getMonthlyLimit()));
                    }
                    return java.util.Optional.empty();
                }).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(UUID id) {
        Transaction transaction = transactionRepository.findByIdWithAccount(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactions(Specification<Transaction> spec, Pageable pageable) {
        Page<Transaction> idPage = transactionRepository.findAll(spec, pageable);
        if (idPage.isEmpty()) {
            return Page.empty(pageable);
        }
        
        Page<Transaction> fetchPage = transactionRepository.findAllWithAccountByIds(
                idPage.map(Transaction::getId).getContent(), 
                pageable);
                
        return fetchPage.map(transactionMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteTransaction(UUID id) {
        Transaction transaction = transactionRepository.findByIdWithAccount(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
                
        Account account = transaction.getAccount();
        account.revertTransaction(transaction.getType(), transaction.getAmount());
        
        transactionRepository.delete(transaction);
        accountRepository.save(account);
    }
}
