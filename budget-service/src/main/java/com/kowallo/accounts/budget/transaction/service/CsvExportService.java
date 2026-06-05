package com.kowallo.accounts.budget.transaction.service;

import com.kowallo.accounts.budget.account.repository.AccountRepository;
import com.kowallo.accounts.budget.common.exception.AccountNotFoundException;
import com.kowallo.accounts.budget.transaction.model.Transaction;
import com.kowallo.accounts.budget.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CsvExportService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public String exportTransactionsToCsv(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }

        List<Transaction> transactions = transactionRepository.findAllByAccountIdOrderByTransactionDateDesc(accountId);

        StringJoiner csv = new StringJoiner("\n");
        csv.add("ID,Date,Amount,Type,Category,Description");

        for (Transaction t : transactions) {
            csv.add(String.format("%s,%s,%s,%s,%s,%s",
                    t.getId(),
                    t.getTransactionDate(),
                    t.getAmount(),
                    t.getType(),
                    escapeCsvField(t.getCategory()),
                    escapeCsvField(t.getDescription())
            ));
        }

        return csv.toString();
    }

    private String escapeCsvField(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\"")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
