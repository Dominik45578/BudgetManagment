package com.kowallo.accounts.budget.budget.mapper;

import com.kowallo.accounts.budget.budget.dto.BudgetResponse;
import com.kowallo.accounts.budget.budget.model.CategoryBudget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountName", source = "account.name")
    BudgetResponse toResponse(CategoryBudget budget);
}
