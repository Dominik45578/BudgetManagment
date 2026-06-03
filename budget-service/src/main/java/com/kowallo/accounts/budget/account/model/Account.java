package com.kowallo.accounts.budget.account.model;

import com.kowallo.accounts.budget.common.model.BaseEntity;
import com.kowallo.accounts.budget.transaction.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "Budget account entity representing a user's financial account")
@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {

    @Schema(description = "Unique name of the account", example = "My Savings")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Schema(description = "Current balance calculated from all transactions", example = "1500.00")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    public Account(String name) {
        this.name = name;
    }

    public void applyTransaction(TransactionType type, BigDecimal amount) {
        this.balance = type.apply(this.balance, amount);
    }

    public void revertTransaction(TransactionType type, BigDecimal amount) {
        this.balance = type.revert(this.balance, amount);
    }
}

