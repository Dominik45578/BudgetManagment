package com.kowallo.accounts.budget.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Request to set a monthly spending limit for a category")
public record CreateBudgetRequest(
        @Schema(description = "ID of the account to assign the budget limit to", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull UUID accountId,

        @Schema(description = "Category name the limit applies to", example = "Groceries", maxLength = 100)
        @NotBlank @Size(max = 100) String category,

        @Schema(description = "Monthly spending limit (must be positive)", example = "500.00")
        @NotNull @Positive BigDecimal monthlyLimit
) {
}

