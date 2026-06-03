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
}
