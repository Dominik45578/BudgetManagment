package com.kowallo.accounts.budget.summary.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Financial summary for an account over a time period")
public record SummaryResponse(
        @Schema(description = "Total income in the period", example = "3000.00")
        BigDecimal totalIncome,

        @Schema(description = "Total expenses in the period", example = "1500.00")
        BigDecimal totalExpenses,

        @Schema(description = "Net balance (income - expenses)", example = "1500.00")
        BigDecimal balance,

        @Schema(description = "Total number of transactions in the period")
        long transactionCount,

        @Schema(description = "Breakdown of expenses by category")
        List<CategoryExpenseDto> categoryExpenses
) {
}

