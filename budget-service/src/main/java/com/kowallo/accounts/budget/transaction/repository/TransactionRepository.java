package com.kowallo.accounts.budget.transaction.repository;

import com.kowallo.accounts.budget.transaction.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
