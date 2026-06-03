package com.kowallo.accounts.budget.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Monthly budget limit for a spending category")
public record BudgetResponse(
        @Schema(description = "Unique budget limit identifier")
        UUID id,

        @Schema(description = "ID of the associated account")
        UUID accountId,

        @Schema(description = "Name of the associated account", example = "My Savings")
        String accountName,

        @Schema(description = "Category this limit applies to", example = "Groceries")
        String category,

        @Schema(description = "Monthly spending limit", example = "500.00")
        BigDecimal monthlyLimit,

        @Schema(description = "Timestamp when the budget limit was created")
        LocalDateTime createdAt
) {
}

