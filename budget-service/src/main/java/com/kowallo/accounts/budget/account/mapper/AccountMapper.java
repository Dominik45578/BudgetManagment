package com.kowallo.accounts.budget.account.mapper;

import com.kowallo.accounts.budget.account.dto.AccountResponse;
import com.kowallo.accounts.budget.account.model.Account;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponse toResponse(Account account);

    default Page<AccountResponse> toResponsePage(Page<Account> page) {
        return page.map(this::toResponse);
    }
}
