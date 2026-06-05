package com.kowallo.accounts.budget.budget.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kowallo.accounts.budget.budget.dto.CreateBudgetRequest;
import com.kowallo.accounts.budget.budget.service.BudgetService;
import com.kowallo.accounts.budget.common.exception.BudgetAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BudgetController.class)
class BudgetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BudgetService budgetService;

    @Test
    void createBudget_WhenAlreadyExists_ShouldReturn409Conflict() throws Exception {
        // given
        UUID accountId = UUID.randomUUID();
        CreateBudgetRequest request = new CreateBudgetRequest(accountId, "Groceries", BigDecimal.valueOf(500));
        
        when(budgetService.createBudget(any(CreateBudgetRequest.class)))
                .thenThrow(new BudgetAlreadyExistsException(accountId, "Groceries"));

        // when & then
        mockMvc.perform(post("/api/v1/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Business Exception"))
                .andExpect(jsonPath("$.code").value("BUDGET_ALREADY_EXISTS"));
    }
}
