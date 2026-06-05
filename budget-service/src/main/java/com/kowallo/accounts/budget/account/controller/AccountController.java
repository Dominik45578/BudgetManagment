package com.kowallo.accounts.budget.account.controller;
 
import com.kowallo.accounts.budget.account.dto.AccountResponse;
import com.kowallo.accounts.budget.account.dto.CreateAccountRequest;
import com.kowallo.accounts.budget.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.kowallo.accounts.budget.transaction.service.CsvExportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Operations related to managing accounts")
public class AccountController {

    private final AccountService accountService;
    private final CsvExportService csvExportService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new account", description = "Creates a new financial account with a unique name.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "409", description = "Account with this name already exists")
    })
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID", description = "Fetches account details by its unique identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public AccountResponse getAccount(
            @PathVariable @Parameter(description = "UUID of the account to fetch") UUID id) {
        return accountService.getAccount(id);
    }

    @GetMapping
    @Operation(summary = "Get all accounts", description = "Retrieves a paginated list of all accounts.")
    @ApiResponse(responseCode = "200", description = "Paginated list of accounts")
    public Page<AccountResponse> getAllAccounts(Pageable pageable) {
        return accountService.getAllAccounts(pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete account by ID", description = "Deletes an account if it doesn't have any existing transactions.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "409", description = "Account has existing transactions")
    })
    public void deleteAccount(
            @PathVariable @Parameter(description = "UUID of the account to delete") UUID id) {
        accountService.deleteAccount(id);
    }

    @GetMapping("/{id}/transactions/export")
    @Operation(summary = "Export transactions to CSV", description = "Exports all transactions of a given account to a CSV file, ordered by date descending.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CSV file with account transactions",
                    content = @Content(mediaType = "text/csv")),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<Resource> exportTransactions(
            @PathVariable @Parameter(description = "UUID of the account") UUID id) {
        String csvData = csvExportService.exportTransactionsToCsv(id);
        
        ByteArrayResource resource = new ByteArrayResource(csvData.getBytes(StandardCharsets.UTF_8));
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"transactions_" + id + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
}
