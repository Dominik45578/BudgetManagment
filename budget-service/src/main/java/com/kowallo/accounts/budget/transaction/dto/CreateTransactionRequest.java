package com.kowallo.accounts.budget.transaction.dto;

import com.kowallo.accounts.budget.transaction.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Request to record a new financial transaction")
public record CreateTransactionRequest(
        @Schema(description = "ID of the account this transaction belongs to", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull UUID accountId,

        @Schema(description = "Transaction amount (must be positive)", example = "150.00")
        @NotNull @Positive BigDecimal amount,

        @Schema(description = "Transaction type", example = "EXPENSE")
        @NotNull TransactionType type,

        @Schema(description = "Category of the transaction", example = "Groceries", maxLength = 100)
        @NotBlank @Size(max = 100) String category,

        @Schema(description = "Optional description of the transaction", example = "Weekly shopping", maxLength = 500)
        @Size(max = 500) String description,

        @Schema(description = "Date and time when the transaction occurred", example = "2026-06-03T12:30:00")
        @NotNull LocalDateTime transactionDate
) {
}

