package com.kowallo.accounts.budget.transaction.model;

import com.kowallo.accounts.budget.account.model.Account;
import com.kowallo.accounts.budget.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Transaction entity representing a single financial transfer (income or expense)")
@Entity
@Table(name = "transaction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @Schema(description = "The account this transaction belongs to")
    private Account account;

    @Column(nullable = false, precision = 19, scale = 2)
    @Schema(description = "Transaction amount, must be greater than 0", example = "150.50", minimum = "0.01")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Schema(description = "Transaction type: INCOME or EXPENSE", example = "EXPENSE")
    private TransactionType type;

    @Column(nullable = false, length = 100)
    @Schema(description = "Transaction category", example = "Groceries")
    private String category;

    @Column(length = 500)
    @Schema(description = "Optional transaction description", example = "Weekly grocery shopping", nullable = true)
    private String description;

    @Column(name = "transaction_date", nullable = false)
    @Schema(description = "Date and time when the transaction occurred", example = "2026-06-05T12:00:00")
    private LocalDateTime transactionDate;

    @Builder
    public Transaction(Account account, BigDecimal amount, TransactionType type,
                       String category, String description, LocalDateTime transactionDate) {
        this.account = account;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.description = description;
        this.transactionDate = transactionDate;
    }
}
