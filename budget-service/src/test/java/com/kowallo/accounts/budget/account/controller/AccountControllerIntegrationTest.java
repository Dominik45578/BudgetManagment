package com.kowallo.accounts.budget.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kowallo.accounts.budget.account.dto.AccountResponse;
import com.kowallo.accounts.budget.account.dto.CreateAccountRequest;
import com.kowallo.accounts.budget.account.service.AccountService;
import com.kowallo.accounts.budget.common.exception.AccountAlreadyExistsException;
import com.kowallo.accounts.budget.common.exception.AccountNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.kowallo.accounts.budget.transaction.service.CsvExportService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private CsvExportService csvExportService;

    @Test
    void createAccount_WhenValid_ShouldReturn201AndResponse() throws Exception {
        // given
        CreateAccountRequest request = new CreateAccountRequest("Savings");
        AccountResponse response = new AccountResponse(UUID.randomUUID(), "Savings", BigDecimal.ZERO, LocalDateTime.now());
        
        when(accountService.createAccount(any(CreateAccountRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Savings"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    void createAccount_WhenNameExists_ShouldReturn409Conflict() throws Exception {
        // given
        CreateAccountRequest request = new CreateAccountRequest("Savings");
        when(accountService.createAccount(any(CreateAccountRequest.class)))
                .thenThrow(new AccountAlreadyExistsException("Savings"));

        // when & then
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Business Exception"))
                .andExpect(jsonPath("$.code").value("ACCOUNT_ALREADY_EXISTS"));
    }

    @Test
    void getAccount_WhenNotExists_ShouldReturn404NotFound() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        when(accountService.getAccount(id))
                .thenThrow(new AccountNotFoundException(id));

        // when & then
        mockMvc.perform(get("/api/v1/accounts/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Business Exception"))
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
    }
}
