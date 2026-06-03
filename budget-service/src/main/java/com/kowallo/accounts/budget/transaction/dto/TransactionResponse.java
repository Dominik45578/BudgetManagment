package com.kowallo.accounts.budget.transaction.dto;

import com.kowallo.accounts.budget.transaction.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Transaction details returned by the API")
public record TransactionResponse(
        @Schema(description = "Unique transaction identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "ID of the account this transaction belongs to")
        UUID accountId,

        @Schema(description = "Name of the account this transaction belongs to", example = "My Savings")
        String accountName,

        @Schema(description = "Transaction amount", example = "150.00")
        BigDecimal amount,

        @Schema(description = "Transaction type: INCOME or EXPENSE")
        TransactionType type,

        @Schema(description = "Category of the transaction", example = "Groceries")
        String category,

        @Schema(description = "Optional description", example = "Weekly shopping")
        String description,

        @Schema(description = "Date and time when the transaction occurred")
        LocalDateTime transactionDate,

        @Schema(description = "Timestamp when the record was created in the system")
        LocalDateTime createdAt,

        @Schema(description = "Budget warning message when monthly category limit is exceeded", nullable = true, example = "Budget limit exceeded for Groceries: 200.00 / 150.00")
        String budgetWarning
) {
}

