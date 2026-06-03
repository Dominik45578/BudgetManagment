package com.kowallo.accounts.budget.budget.model;

import com.kowallo.accounts.budget.account.model.Account;
import com.kowallo.accounts.budget.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "category_budget", uniqueConstraints =
        @UniqueConstraint(columnNames = {"account_id", "category"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryBudget extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(name = "monthly_limit", nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyLimit;

    public CategoryBudget(Account account, String category, BigDecimal monthlyLimit) {
        this.account = account;
        this.category = category;
        this.monthlyLimit = monthlyLimit;
    }
}
