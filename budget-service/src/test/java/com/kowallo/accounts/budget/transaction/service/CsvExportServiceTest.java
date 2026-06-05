package com.kowallo.accounts.budget.transaction.service;

import com.kowallo.accounts.budget.account.model.Account;
import com.kowallo.accounts.budget.account.repository.AccountRepository;
import com.kowallo.accounts.budget.common.exception.AccountNotFoundException;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvExportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CsvExportService csvExportService;

    @Test
    void exportToCsv_WhenAccountNotFound_ShouldThrowException() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.existsById(accountId)).thenReturn(false);

        assertThatThrownBy(() -> csvExportService.exportTransactionsToCsv(accountId))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void exportToCsv_WhenNoTransactions_ShouldReturnOnlyHeader() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.existsById(accountId)).thenReturn(true);
        when(transactionRepository.findAllByAccountIdOrderByTransactionDateDesc(accountId))
                .thenReturn(List.of());

        String csv = csvExportService.exportTransactionsToCsv(accountId);

        assertThat(csv).isEqualTo("ID,Date,Amount,Type,Category,Description");
    }

    @Test
    void exportToCsv_ShouldContainTransactionData() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account("Savings");
        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(BigDecimal.valueOf(150.50))
                .type(TransactionType.EXPENSE)
                .category("Food")
                .description("Lunch")
                .transactionDate(LocalDateTime.of(2026, 6, 5, 12, 0))
                .build();

        when(accountRepository.existsById(accountId)).thenReturn(true);
        when(transactionRepository.findAllByAccountIdOrderByTransactionDateDesc(accountId))
                .thenReturn(List.of(transaction));

        String csv = csvExportService.exportTransactionsToCsv(accountId);
        String dataLine = csv.lines().skip(1).findFirst().orElse("");

        assertThat(dataLine).contains("150.5");
        assertThat(dataLine).contains("EXPENSE");
        assertThat(dataLine).contains("Food");
        assertThat(dataLine).contains("Lunch");
    }

    @Test
    void exportToCsv_WhenCategoryContainsComma_ShouldWrapInQuotes() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account("Savings");
        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(BigDecimal.valueOf(50))
                .type(TransactionType.EXPENSE)
                .category("Food, beverages")
                .description("Lunch")
                .transactionDate(LocalDateTime.now())
                .build();

        when(accountRepository.existsById(accountId)).thenReturn(true);
        when(transactionRepository.findAllByAccountIdOrderByTransactionDateDesc(accountId))
                .thenReturn(List.of(transaction));

        String csv = csvExportService.exportTransactionsToCsv(accountId);

        assertThat(csv).contains("\"Food, beverages\"");
    }

    @Test
    void exportToCsv_WhenDescriptionContainsComma_ShouldWrapInQuotes() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account("Savings");
        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(BigDecimal.valueOf(50))
                .type(TransactionType.EXPENSE)
                .category("Food")
                .description("Coffee, tea, juice")
                .transactionDate(LocalDateTime.now())
                .build();

        when(accountRepository.existsById(accountId)).thenReturn(true);
        when(transactionRepository.findAllByAccountIdOrderByTransactionDateDesc(accountId))
                .thenReturn(List.of(transaction));

        String csv = csvExportService.exportTransactionsToCsv(accountId);

        assertThat(csv).contains("\"Coffee, tea, juice\"");
    }

    @Test
    void exportToCsv_WhenDescriptionContainsQuote_ShouldDoubleEscape() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account("Savings");
        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(BigDecimal.valueOf(50))
                .type(TransactionType.EXPENSE)
                .category("Food")
                .description("He said \"hello\"")
                .transactionDate(LocalDateTime.now())
                .build();

        when(accountRepository.existsById(accountId)).thenReturn(true);
        when(transactionRepository.findAllByAccountIdOrderByTransactionDateDesc(accountId))
                .thenReturn(List.of(transaction));

        String csv = csvExportService.exportTransactionsToCsv(accountId);

        assertThat(csv).contains("\"He said \"\"hello\"\"\"");
    }

    @Test
    void exportToCsv_WhenNullDescription_ShouldNotFail() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account("Savings");
        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(BigDecimal.valueOf(200))
                .type(TransactionType.INCOME)
                .category("Salary")
                .description(null)
                .transactionDate(LocalDateTime.now())
                .build();

        when(accountRepository.existsById(accountId)).thenReturn(true);
        when(transactionRepository.findAllByAccountIdOrderByTransactionDateDesc(accountId))
                .thenReturn(List.of(transaction));

        String csv = csvExportService.exportTransactionsToCsv(accountId);
        String dataLine = csv.lines().skip(1).findFirst().orElse("");

        assertThat(dataLine).contains("INCOME");
        assertThat(dataLine).contains("Salary");
        assertThat(dataLine).endsWith(",");
    }
}
