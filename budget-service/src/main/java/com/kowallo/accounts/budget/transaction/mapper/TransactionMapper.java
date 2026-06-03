package com.kowallo.accounts.budget.transaction.mapper;

import com.kowallo.accounts.budget.transaction.dto.TransactionResponse;
import com.kowallo.accounts.budget.transaction.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountName", source = "account.name")
    @Mapping(target = "budgetWarning", ignore = true)
    TransactionResponse toResponse(Transaction transaction);

    default TransactionResponse toResponse(Transaction transaction, String budgetWarning) {
        TransactionResponse base = toResponse(transaction);
        return new TransactionResponse(
                base.id(),
                base.accountId(),
                base.accountName(),
                base.amount(),
                base.type(),
                base.category(),
                base.description(),
                base.transactionDate(),
                base.createdAt(),
                budgetWarning
        );
    }
}

