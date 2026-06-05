package com.kowallo.accounts.budget.summary.service;

import com.kowallo.accounts.budget.account.repository.AccountRepository;
import com.kowallo.accounts.budget.common.exception.AccountNotFoundException;
import com.kowallo.accounts.budget.summary.dto.CategoryExpenseDto;
import com.kowallo.accounts.budget.summary.dto.SummaryResponse;
import com.kowallo.accounts.budget.transaction.model.Transaction;
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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

        List<Transaction> transactions = transactionRepository.findAllByAccountIdAndDateRange(accountId, startOfMonth, endOfMonth);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) {
                totalIncome = totalIncome.add(t.getAmount());
            } else if (t.getType() == TransactionType.EXPENSE) {
                totalExpenses = totalExpenses.add(t.getAmount());
            }
        }

        BigDecimal balance = totalIncome.subtract(totalExpenses);

        Map<String, BigDecimal> expenseByCategory = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        final BigDecimal finalTotalExpenses = totalExpenses;
        List<CategoryExpenseDto> categoryExpenses = expenseByCategory.entrySet().stream()
                .map(entry -> {
                    BigDecimal percentage = BigDecimal.ZERO;
                    if (finalTotalExpenses.compareTo(BigDecimal.ZERO) > 0) {
                        percentage = entry.getValue().multiply(BigDecimal.valueOf(100))
                                .divide(finalTotalExpenses, 2, RoundingMode.HALF_UP);
                    }
                    return new CategoryExpenseDto(entry.getKey(), entry.getValue(), percentage);
                })
                .sorted((c1, c2) -> c2.total().compareTo(c1.total()))
                .collect(Collectors.toList());

        return new SummaryResponse(totalIncome, totalExpenses, balance, transactions.size(), categoryExpenses);
    }
}
