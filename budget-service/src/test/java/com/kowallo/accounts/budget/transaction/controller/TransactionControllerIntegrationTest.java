package com.kowallo.accounts.budget.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kowallo.accounts.budget.transaction.dto.CreateTransactionRequest;
import com.kowallo.accounts.budget.transaction.model.TransactionType;
import com.kowallo.accounts.budget.transaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    void createTransaction_WhenValidationFails_ShouldReturn400BadRequest() throws Exception {
        // given
        // Amount is negative (-10) and Category is blank -> should trigger @Positive and @NotBlank
        CreateTransactionRequest request = new CreateTransactionRequest(
                UUID.randomUUID(),
                BigDecimal.valueOf(-10),
                TransactionType.EXPENSE,
                "   ",
                "Validation Test",
                LocalDateTime.now()
        );

        // when & then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.errors.amount").exists())
                .andExpect(jsonPath("$.errors.category").exists());
    }
}
