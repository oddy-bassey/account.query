package com.revoltcode.account.query.infrastructure.handler;

import com.revoltcode.account.common.event.*;
import com.revoltcode.account.query.domain.repository.AccountRepository;
import com.revoltcode.account.query.domain.model.BankAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountEventHandler implements EventHandler {

    @Autowired
    private final AccountRepository accountRepository;

    @Override
    public void on(AccountOpenedEvent event) {
        var bankAccount = BankAccount.builder()
                .id(event.getId())
                .customerId(event.getCustomerId())
                .createdDate(event.getCreatedDate())
                .accountType(event.getAccountType())
                .balance(event.getOpeningBalance())
                .build();

        accountRepository.save(bankAccount);
    }

    @Override
    public void on(FundsDepositedEvent event) {
        var bankAccount = accountRepository.findById(event.getId()).orElseThrow();

        var currentBalance = bankAccount.getBalance();
        var latestBalance = currentBalance + event.getAmount();
        bankAccount.setBalance(latestBalance);
        accountRepository.save(bankAccount);
    }

    @Override
    public void on(FundsWithdrawnEvent event) {
        var bankAccount = accountRepository.findById(event.getId()).orElseThrow();

        var currentBalance = bankAccount.getBalance();
        var latestBalance = currentBalance - event.getAmount();
        bankAccount.setBalance(latestBalance);
        accountRepository.save(bankAccount);
    }

    @Override
    public void on(AccountClosedEvent event) {
        accountRepository.deleteById(event.getId());
    }
}
