package com.kowallo.accounts.budget.transaction.repository;

import com.kowallo.accounts.budget.summary.dto.CategorySumDto;
import com.kowallo.accounts.budget.transaction.model.Transaction;
import com.kowallo.accounts.budget.transaction.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    boolean existsByAccountId(UUID accountId);

    @Query("SELECT t FROM Transaction t JOIN FETCH t.account WHERE t.id = :id")
    Optional<Transaction> findByIdWithAccount(@Param("id") UUID id);

    @Query(
        value = "SELECT DISTINCT t FROM Transaction t JOIN FETCH t.account a WHERE t.id IN :ids",
        countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.id IN :ids"
    )
    Page<Transaction> findAllWithAccountByIds(@Param("ids") Iterable<UUID> ids, Pageable pageable);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.account.id = :accountId AND LOWER(t.category) = LOWER(:category) " +
           "AND t.type = 'EXPENSE' AND t.transactionDate >= :startDate AND t.transactionDate < :endDate")
    BigDecimal sumExpensesByAccountAndCategoryInDateRange(
            @Param("accountId") UUID accountId,
            @Param("category") String category,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.account.id = :accountId AND t.type = :type " +
           "AND t.transactionDate >= :startDate AND t.transactionDate < :endDate")
    BigDecimal sumByAccountAndTypeInDateRange(
            @Param("accountId") UUID accountId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT new com.kowallo.accounts.budget.summary.dto.CategorySumDto(t.category, SUM(t.amount)) " +
           "FROM Transaction t " +
           "WHERE t.account.id = :accountId AND t.type = 'EXPENSE' " +
           "AND t.transactionDate >= :startDate AND t.transactionDate < :endDate " +
           "GROUP BY t.category")
    List<CategorySumDto> sumExpensesByCategoryInDateRange(
            @Param("accountId") UUID accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(t) FROM Transaction t " +
           "WHERE t.account.id = :accountId " +
           "AND t.transactionDate >= :startDate AND t.transactionDate < :endDate")
    long countByAccountIdAndDateRange(
            @Param("accountId") UUID accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    List<Transaction> findAllByAccountIdOrderByTransactionDateDesc(UUID accountId);
}

