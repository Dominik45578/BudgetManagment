package com.kowallo.accounts.budget.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Account details returned by the API")
public record AccountResponse(
        @Schema(description = "Unique account identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Unique account name", example = "My Savings")
        String name,

        @Schema(description = "Current balance of the account", example = "1500.00")
        BigDecimal balance,

        @Schema(description = "Timestamp when the account was created")
        LocalDateTime createdAt
) {
}
