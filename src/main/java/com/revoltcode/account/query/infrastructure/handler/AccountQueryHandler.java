package com.revoltcode.account.query.infrastructure.handler;

import com.revoltcode.account.query.domain.model.BankAccount;
import com.revoltcode.account.query.domain.repository.AccountRepository;
import com.revoltcode.account.query.query.FindAccountByCustomerIdAndAccountTypeQuery;
import com.revoltcode.account.query.query.FindAccountByCustomerIdQuery;
import com.revoltcode.account.query.query.FindAccountByIdQuery;
import com.revoltcode.account.query.query.FindAllAccountQuery;
import com.revoltcode.cqrs.core.domain.model.BaseEntity;
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
    public List<BaseEntity> handle(FindAccountByCustomerIdQuery query) {
        var bankAccount = accountRepository.findByCustomerId(query.getCustomerId());
        if(bankAccount.isEmpty()) return null;
        List<BaseEntity> bankAccountList = new ArrayList<>();
        bankAccountList.add(bankAccount.get());
        return bankAccountList;
    }

    @Override
    public List<BaseEntity> handle(FindAccountByCustomerIdAndAccountTypeQuery query) {
        var bankAccount = accountRepository.findByCustomerIdAndAccountType(query.getCustomerId(), query.getAccountType());
        if(bankAccount.isEmpty()) return null;
        List<BaseEntity> bankAccountList = new ArrayList<>();
        bankAccountList.add(bankAccount.get());
        return bankAccountList;
    }
}