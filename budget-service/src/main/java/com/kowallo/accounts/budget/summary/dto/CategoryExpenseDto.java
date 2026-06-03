package com.kowallo.accounts.budget.summary.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Aggregated expense for a single spending category")
public record CategoryExpenseDto(
        @Schema(description = "Category name", example = "Groceries")
        String category,

        @Schema(description = "Total amount spent in this category", example = "320.00")
        BigDecimal total,

        @Schema(description = "Percentage of total expenses this category represents", example = "21.33")
        BigDecimal percentage
) {
}

