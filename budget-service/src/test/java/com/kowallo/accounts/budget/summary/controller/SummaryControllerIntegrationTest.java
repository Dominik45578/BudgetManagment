package com.kowallo.accounts.budget.summary.controller;

import com.kowallo.accounts.budget.summary.service.SummaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SummaryController.class)
class SummaryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SummaryService summaryService;

    @Test
    void getAccountSummary_WhenPeriodFormatInvalid_ShouldReturn400BadRequest() throws Exception {
        // given
        UUID accountId = UUID.randomUUID();

        // when & then
        mockMvc.perform(get("/api/v1/summaries/account/{accountId}", accountId)
                        .param("period", "invalid-date-format"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Type Mismatch"))
                .andExpect(jsonPath("$.detail").value("Parameter 'period' should be of type YearMonth"));
    }
}
