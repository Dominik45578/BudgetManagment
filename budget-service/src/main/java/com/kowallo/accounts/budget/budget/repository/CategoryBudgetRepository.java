package com.kowallo.accounts.budget.budget.repository;

import com.kowallo.accounts.budget.budget.model.CategoryBudget;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryBudgetRepository extends JpaRepository<CategoryBudget, UUID> {

    Optional<CategoryBudget> findByAccountIdAndCategoryIgnoreCase(UUID accountId, String category);

    @EntityGraph(attributePaths = {"account"})
    List<CategoryBudget> findAllByAccountId(UUID accountId);

    boolean existsByAccountId(UUID accountId);
}

