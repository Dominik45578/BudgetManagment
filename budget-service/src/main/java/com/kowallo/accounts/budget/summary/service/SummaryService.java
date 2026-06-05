package com.kowallo.accounts.budget.summary.service;

import com.kowallo.accounts.budget.summary.dto.SummaryResponse;

import java.time.YearMonth;
import java.util.UUID;

public interface SummaryService {
    SummaryResponse getAccountSummary(UUID accountId, YearMonth period);
}
