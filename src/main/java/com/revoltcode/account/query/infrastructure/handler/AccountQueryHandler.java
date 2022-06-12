package com.revoltcode.account.query.api.queries;

import com.revoltcode.account.query.api.dto.EqualityType;
import com.revoltcode.account.query.api.domain.AccountRepository;
import com.revoltcode.account.query.api.domain.BankAccount;
import com.revoltcode.cqrs.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class AccountQueryHandler implements QueryHandler{

    @Autowired
    private final AccountRepository accountRepository;

    @Override
    public List<BaseEntity> handle(FindAllAccountQuery query) {
        Iterable<BankAccount> bankAccounts = accountRepository.findAll();
        List<BaseEntity> bankAccountList = new ArrayList<>();
        bankAccounts.forEach(bankAccountList::add);
        return bankAccountList;
    }

    @Override
    public List<BaseEntity> handle(FindAccountByIdQuery query) {
        var bankAccount = accountRepository.findById(query.getId());
        if(bankAccount.isEmpty()) return null;
        List<BaseEntity> bankAccountList = new ArrayList<>();
        bankAccountList.add(bankAccount.get());
        return bankAccountList;
    }

    @Override
    public List<BaseEntity> handle(FindAccountByHolderQuery query) {
        var bankAccount = accountRepository.findByAccountHolder(query.getAccountHolder());
        if(bankAccount.isEmpty()) return null;
        List<BaseEntity> bankAccountList = new ArrayList<>();
        bankAccountList.add(bankAccount.get());
        return bankAccountList;
    }

    @Override
    public List<BaseEntity> handle(FindAccountsWithBalanceQuery query) {
        List<BaseEntity> bankAccountList = query.getEqualityType().equals(EqualityType.GREATER_THAN)?
                accountRepository.findByBalanceGreaterThan(query.getBalance()) :
                accountRepository.findByBalanceLessThan(query.getBalance());
        return bankAccountList;
    }
}