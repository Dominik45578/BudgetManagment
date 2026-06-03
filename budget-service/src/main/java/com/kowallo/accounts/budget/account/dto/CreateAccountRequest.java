package com.kowallo.accounts.budget.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to create a new account")
public record CreateAccountRequest(
        @Schema(description = "Unique name of the account", example = "My Savings", maxLength = 100)
        @NotBlank @Size(max = 100) String name
) {
}
