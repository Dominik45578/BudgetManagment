package com.kowallo.accounts.budget.transaction.controller;

import com.kowallo.accounts.budget.transaction.dto.CreateTransactionRequest;
import com.kowallo.accounts.budget.transaction.dto.TransactionResponse;
import com.kowallo.accounts.budget.transaction.specification.TransactionSpecification;
import com.kowallo.accounts.budget.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Operations related to managing financial transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new transaction", description = "Creates a new transaction (INCOME/EXPENSE) and updates the associated account balance.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or transaction logic violation"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public TransactionResponse createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        return transactionService.createTransaction(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID", description = "Retrieves details of a financial transaction by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public TransactionResponse getTransaction(
            @PathVariable @Parameter(description = "UUID of the transaction to fetch") UUID id) {
        return transactionService.getTransaction(id);
    }

    @GetMapping
    @Operation(summary = "Search/Filter transactions", description = "Searches and filters transactions by account ID, category, or date range, with pagination.")
    @ApiResponse(responseCode = "200", description = "Paginated list of transactions matching criteria")
    public Page<TransactionResponse> getTransactions(
            @RequestParam(required = false) @Parameter(description = "Filter by account UUID") UUID accountId,
            @RequestParam(required = false) @Parameter(description = "Filter by category name") String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Filter from date (inclusive)") LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Filter to date (inclusive)") LocalDateTime to,
            Pageable pageable) {
        return transactionService.getTransactions(
                TransactionSpecification.build(accountId, category, from, to), pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete transaction", description = "Deletes a transaction by ID and reverts its effect on the account balance.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public void deleteTransaction(@PathVariable @Parameter(description = "UUID of the transaction to delete") UUID id) {
        transactionService.deleteTransaction(id);
    }
}
