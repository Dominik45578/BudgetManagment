package com.kowallo.accounts.budget.budget.controller;

import com.kowallo.accounts.budget.budget.dto.BudgetResponse;
import com.kowallo.accounts.budget.budget.dto.CreateBudgetRequest;
import com.kowallo.accounts.budget.budget.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
@Tag(name = "Budgets", description = "Operations related to managing monthly category budget limits")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a monthly budget limit", description = "Sets a new monthly spending limit for a specific category and account.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Budget created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "409", description = "Budget for this category and account already exists")
    })
    public BudgetResponse createBudget(@Valid @RequestBody CreateBudgetRequest request) {
        return budgetService.createBudget(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get budget by ID", description = "Fetches details of a monthly category budget limit by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    public BudgetResponse getBudget(
            @PathVariable @Parameter(description = "UUID of the budget to fetch") UUID id) {
        return budgetService.getBudget(id);
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get budgets by Account ID", description = "Retrieves all monthly category budget limits assigned to a specific account.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of budgets retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public List<BudgetResponse> getBudgetsByAccountId(
            @PathVariable @Parameter(description = "UUID of the account to fetch budgets for") UUID accountId) {
        return budgetService.getBudgetsByAccountId(accountId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete budget limit", description = "Deletes a monthly category budget limit by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Budget deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    public void deleteBudget(
            @PathVariable @Parameter(description = "UUID of the budget limit to delete") UUID id) {
        budgetService.deleteBudget(id);
    }
}
