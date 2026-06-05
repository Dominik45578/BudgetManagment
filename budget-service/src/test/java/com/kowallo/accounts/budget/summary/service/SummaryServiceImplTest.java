package com.kowallo.accounts.budget.summary.service;

import com.kowallo.accounts.budget.account.repository.AccountRepository;
import com.kowallo.accounts.budget.common.exception.AccountNotFoundException;
import com.kowallo.accounts.budget.summary.dto.SummaryResponse;
import com.kowallo.accounts.budget.transaction.model.Transaction;
import com.kowallo.accounts.budget.transaction.model.TransactionType;
import com.kowallo.accounts.budget.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SummaryServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private SummaryServiceImpl summaryService;

    @Test
    void getAccountSummary_WhenExists_ShouldReturnCalculatedSummary() {
        // given
        UUID accountId = UUID.randomUUID();
        YearMonth period = YearMonth.of(2026, 6);
        when(accountRepository.existsById(accountId)).thenReturn(true);
        
        Transaction t1 = Transaction.builder().amount(BigDecimal.valueOf(1000)).type(TransactionType.INCOME).category("Salary").build();
        Transaction t2 = Transaction.builder().amount(BigDecimal.valueOf(200)).type(TransactionType.EXPENSE).category("Groceries").build();
        Transaction t3 = Transaction.builder().amount(BigDecimal.valueOf(100)).type(TransactionType.EXPENSE).category("Groceries").build();
        Transaction t4 = Transaction.builder().amount(BigDecimal.valueOf(100)).type(TransactionType.EXPENSE).category("Transport").build();
        
        when(transactionRepository.findAllByAccountIdAndDateRange(eq(accountId), any(), any())).thenReturn(List.of(t1, t2, t3, t4));

        // when
        SummaryResponse response = summaryService.getAccountSummary(accountId, period);

        // then
        assertThat(response.totalIncome()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(response.totalExpenses()).isEqualTo(BigDecimal.valueOf(400));
        assertThat(response.balance()).isEqualTo(BigDecimal.valueOf(600));
        assertThat(response.transactionCount()).isEqualTo(4);
        assertThat(response.categoryExpenses()).hasSize(2);
        assertThat(response.categoryExpenses().get(0).category()).isEqualTo("Groceries");
        assertThat(response.categoryExpenses().get(0).total()).isEqualTo(BigDecimal.valueOf(300));
        assertThat(response.categoryExpenses().get(0).percentage()).isEqualTo(BigDecimal.valueOf(75).setScale(2)); // 300 / 400 * 100
        assertThat(response.categoryExpenses().get(1).category()).isEqualTo("Transport");
        assertThat(response.categoryExpenses().get(1).total()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(response.categoryExpenses().get(1).percentage()).isEqualTo(BigDecimal.valueOf(25).setScale(2)); // 100 / 400 * 100
    }

    @Test
    void getAccountSummary_WhenAccountNotFound_ShouldThrowException() {
        // given
        UUID accountId = UUID.randomUUID();
        when(accountRepository.existsById(accountId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> summaryService.getAccountSummary(accountId, YearMonth.now()))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
