package com.revoltcode.account.query.api.queries;

import com.revoltcode.cqrs.core.domain.BaseEntity;

import java.util.List;

public interface QueryHandler {
    List<BaseEntity> handle(FindAllAccountQuery query);
    List<BaseEntity> handle(FindAccountByIdQuery query);
    List<BaseEntity> handle(FindAccountByHolderQuery query);
    List<BaseEntity> handle(FindAccountsWithBalanceQuery query);
}
