package com.kowallo.accounts.budget.transaction.model;

import com.kowallo.accounts.budget.account.model.Account;
import com.kowallo.accounts.budget.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(length = 500)
    private String description;

    @Column(name = "transaction_date", nullable = false)
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
