package com.kowallo.accounts.budget.summary.service;

import com.kowallo.accounts.budget.account.repository.AccountRepository;
import com.kowallo.accounts.budget.common.exception.AccountNotFoundException;
import com.kowallo.accounts.budget.summary.dto.CategorySumDto;
import com.kowallo.accounts.budget.summary.dto.SummaryResponse;
import com.kowallo.accounts.budget.transaction.model.TransactionType;
import com.kowallo.accounts.budget.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SummaryServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private SummaryServiceImpl summaryService;

    @Test
    void getAccountSummary_WhenExists_ShouldReturnAggregatedSummary() {
        // given
        UUID accountId = UUID.randomUUID();
        YearMonth period = YearMonth.of(2026, 6);

        when(accountRepository.existsById(accountId)).thenReturn(true);
        when(transactionRepository.sumByAccountAndTypeInDateRange(eq(accountId), eq(TransactionType.INCOME), any(), any()))
                .thenReturn(BigDecimal.valueOf(1000));
        when(transactionRepository.sumByAccountAndTypeInDateRange(eq(accountId), eq(TransactionType.EXPENSE), any(), any()))
                .thenReturn(BigDecimal.valueOf(400));
        when(transactionRepository.countByAccountIdAndDateRange(eq(accountId), any(), any()))
                .thenReturn(4L);
        when(transactionRepository.sumExpensesByCategoryInDateRange(eq(accountId), any(), any()))
                .thenReturn(List.of(
                        new CategorySumDto("Groceries", BigDecimal.valueOf(300)),
                        new CategorySumDto("Transport", BigDecimal.valueOf(100))
                ));

        // when
        SummaryResponse response = summaryService.getAccountSummary(accountId, period);

        // then
        assertThat(response.totalIncome()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(response.totalExpenses()).isEqualByComparingTo(BigDecimal.valueOf(400));
        assertThat(response.balance()).isEqualByComparingTo(BigDecimal.valueOf(600));
        assertThat(response.transactionCount()).isEqualTo(4L);
        assertThat(response.categoryExpenses()).hasSize(2);
        assertThat(response.categoryExpenses().get(0).category()).isEqualTo("Groceries");
        assertThat(response.categoryExpenses().get(0).total()).isEqualByComparingTo(BigDecimal.valueOf(300));
        assertThat(response.categoryExpenses().get(0).percentage()).isEqualByComparingTo("75.00");
        assertThat(response.categoryExpenses().get(1).category()).isEqualTo("Transport");
        assertThat(response.categoryExpenses().get(1).percentage()).isEqualByComparingTo("25.00");
    }

    @Test
    void getAccountSummary_WhenAccountNotFound_ShouldThrowException() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.existsById(accountId)).thenReturn(false);

        assertThatThrownBy(() -> summaryService.getAccountSummary(accountId, YearMonth.now()))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
