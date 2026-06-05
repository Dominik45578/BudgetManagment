package com.kowallo.accounts.budget.summary.controller;

import com.kowallo.accounts.budget.summary.dto.SummaryResponse;
import com.kowallo.accounts.budget.summary.service.SummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/summaries")
@RequiredArgsConstructor
@Tag(name = "Summaries", description = "Operations related to retrieving account financial summaries")
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get account summary", description = "Calculates and retrieves financial summary (total income, total expenses, balance, breakdown by category) for an account over a specific month.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Summary generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date period format"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public SummaryResponse getAccountSummary(
            @PathVariable @Parameter(description = "UUID of the account to analyze") UUID accountId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") @Parameter(description = "Period in yyyy-MM format", example = "2026-06") YearMonth period) {
        return summaryService.getAccountSummary(accountId, period);
    }
}
