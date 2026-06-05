package com.kowallo.accounts.budget.transaction.specification;

import com.kowallo.accounts.budget.transaction.model.Transaction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TransactionSpecification {

    public static Specification<Transaction> hasCategory(String category) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("category")), category.toLowerCase());
    }

    public static Specification<Transaction> dateFrom(LocalDateTime from) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("transactionDate"), from);
    }

    public static Specification<Transaction> dateTo(LocalDateTime to) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("transactionDate"), to);
    }

    public static Specification<Transaction> forAccount(UUID accountId) {
        return (root, query, cb) -> cb.equal(root.get("account").get("id"), accountId);
    }

    public static Specification<Transaction> build(UUID accountId, String category, LocalDateTime from, LocalDateTime to) {
        Specification<Transaction> spec = null;
        if (accountId != null) {
            spec = forAccount(accountId);
        }
        if (category != null && !category.isBlank()) {
            spec = spec == null ? hasCategory(category) : spec.and(hasCategory(category));
        }
        if (from != null) {
            spec = spec == null ? dateFrom(from) : spec.and(dateFrom(from));
        }
        if (to != null) {
            spec = spec == null ? dateTo(to) : spec.and(dateTo(to));
        }
        return spec;
    }
}
