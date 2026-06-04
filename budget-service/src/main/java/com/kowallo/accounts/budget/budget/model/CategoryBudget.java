package com.kowallo.accounts.budget.budget.model;

import com.kowallo.accounts.budget.account.model.Account;
import com.kowallo.accounts.budget.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "Category budget entity representing monthly expense limits set for specific categories")
@Entity
@Table(name = "category_budget", uniqueConstraints =
        @UniqueConstraint(columnNames = {"account_id", "category"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryBudget extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @Schema(description = "The account this budget limit is defined for")
    private Account account;

    @Column(nullable = false, length = 100)
    @Schema(description = "The transaction category name (case-insensitive)", example = "Groceries")
    private String category;

    @Column(name = "monthly_limit", nullable = false, precision = 19, scale = 2)
    @Schema(description = "Monthly budget limit for the category, must be greater than 0", example = "500.00", minimum = "0.01")
    private BigDecimal monthlyLimit;

    public CategoryBudget(Account account, String category, BigDecimal monthlyLimit) {
        this.account = account;
        this.category = category;
        this.monthlyLimit = monthlyLimit;
    }
}
