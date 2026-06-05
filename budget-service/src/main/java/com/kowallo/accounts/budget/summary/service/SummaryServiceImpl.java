package com.kowallo.accounts.budget.summary.service;

import com.kowallo.accounts.budget.account.repository.AccountRepository;
import com.kowallo.accounts.budget.common.exception.AccountNotFoundException;
import com.kowallo.accounts.budget.summary.dto.CategoryExpenseDto;
import com.kowallo.accounts.budget.summary.dto.CategorySumDto;
import com.kowallo.accounts.budget.summary.dto.SummaryResponse;
import com.kowallo.accounts.budget.transaction.model.TransactionType;
import com.kowallo.accounts.budget.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public SummaryResponse getAccountSummary(UUID accountId, YearMonth period) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }

        LocalDateTime startOfMonth = period.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = period.plusMonths(1).atDay(1).atStartOfDay();

        BigDecimal totalIncome = transactionRepository.sumByAccountAndTypeInDateRange(
                accountId, TransactionType.INCOME, startOfMonth, endOfMonth);
        BigDecimal totalExpenses = transactionRepository.sumByAccountAndTypeInDateRange(
                accountId, TransactionType.EXPENSE, startOfMonth, endOfMonth);
        BigDecimal balance = totalIncome.subtract(totalExpenses);
        long transactionCount = transactionRepository.countByAccountIdAndDateRange(
                accountId, startOfMonth, endOfMonth);

        List<CategorySumDto> categorySums = transactionRepository.sumExpensesByCategoryInDateRange(
                accountId, startOfMonth, endOfMonth);

        List<CategoryExpenseDto> categoryExpenses = categorySums.stream()
                .map(cs -> {
                    BigDecimal percentage = BigDecimal.ZERO;
                    if (totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
                        percentage = cs.total().multiply(BigDecimal.valueOf(100))
                                .divide(totalExpenses, 2, RoundingMode.HALF_UP);
                    }
                    return new CategoryExpenseDto(cs.category(), cs.total(), percentage);
                })
                .sorted((a, b) -> b.total().compareTo(a.total()))
                .toList();

        return new SummaryResponse(totalIncome, totalExpenses, balance, transactionCount, categoryExpenses);
    }
}
