package com.kowallo.accounts.budget.transaction.model;

import java.math.BigDecimal;

public enum TransactionType {

    INCOME {
        @Override
        public BigDecimal apply(BigDecimal balance, BigDecimal amount) {
            return balance.add(amount);
        }

        @Override
        public BigDecimal revert(BigDecimal balance, BigDecimal amount) {
            return balance.subtract(amount);
        }
    },

    EXPENSE {
        @Override
        public BigDecimal apply(BigDecimal balance, BigDecimal amount) {
            return balance.subtract(amount);
        }

        @Override
        public BigDecimal revert(BigDecimal balance, BigDecimal amount) {
            return balance.add(amount);
        }
    };

    public abstract BigDecimal apply(BigDecimal balance, BigDecimal amount);

    public abstract BigDecimal revert(BigDecimal balance, BigDecimal amount);
}
