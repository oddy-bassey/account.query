package com.revoltcode.account.query.infrastructure.handler;

import com.revoltcode.account.common.event.account.*;
import com.revoltcode.account.common.event.transaction.DepositedTransactionEvent;
import com.revoltcode.account.common.event.transaction.TransferTransactionEvent;
import com.revoltcode.account.common.event.transaction.WithdrawnTransactionEvent;
import com.revoltcode.account.common.exception.AccountNotFoundException;
import com.revoltcode.account.query.repository.AccountRepository;
import com.revoltcode.account.query.domain.model.BankAccount;
import com.revoltcode.account.query.infrastructure.producer.TransactionEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountEventHandler implements EventHandler {

    private final AccountRepository accountRepository;
    private final TransactionEventProducer transactionEventProducer;

    @Override
    public void on(AccountOpenedEvent event) {
        var bankAccount = BankAccount.builder()
                .id(event.getId())
                .customerId(event.getCustomerId())
                .name(event.getName())
                .accountType(event.getAccountType())
                .balance(event.getOpeningBalance())
                .createdDate(event.getCreatedDate())
                .lastUpdatedDate(event.getCreatedDate())
                .build();

        accountRepository.save(bankAccount);
    }

    @Override
    public void on(FundsDepositedEvent event) {
        var bankAccount = accountRepository.findById(event.getId())
                .orElseThrow(() -> new AccountNotFoundException(
                        MessageFormat.format("Account with id: {0} does not exist!", event.getId())));

        var transactionTime = LocalDateTime.now();
        bankAccount.setBalance(bankAccount.getBalance() + event.getAmount());
        bankAccount.setLastUpdatedDate(transactionTime);

        accountRepository.save(bankAccount);

        var transactionEvent = DepositedTransactionEvent.builder()
                .id(bankAccount.getId())
                .accountName(bankAccount.getName())
                .amount(event.getAmount())
                .description(MessageFormat.format("An amount of {0} was deposited into ({1}) account with id: {2}",
                        event.getAmount(), "", bankAccount.getId()))
                .transactionTime(transactionTime)
                .build();

        transactionEventProducer.produce("DepositedTransactionEvent", transactionEvent);
    }

    @Override
    public void on(FundsWithdrawnEvent event) {
        var bankAccount = accountRepository.findById(event.getId())
                .orElseThrow(() -> new AccountNotFoundException(
                        MessageFormat.format("Account with id: {0} does not exist!", event.getId())));

        var transactionTime = LocalDateTime.now();
        bankAccount.setBalance(bankAccount.getBalance() - event.getAmount());
        bankAccount.setLastUpdatedDate(transactionTime);

        accountRepository.save(bankAccount);

        var transactionEvent = WithdrawnTransactionEvent.builder()
                .id(bankAccount.getId())
                .accountName(bankAccount.getName())
                .amount(event.getAmount())
                .description(MessageFormat.format("An amount of {0} was withdrawn from ({1}) account with id: {2}",
                        event.getAmount(), "", bankAccount.getId()))
                .transactionTime(transactionTime)
                .build();
        transactionEventProducer.produce("WithdrawnTransactionEvent", transactionEvent);
    }

    @Override
    public void on(FundsTransferedEvent event) {
        var debitAccount = accountRepository.findById(event.getId())
                .orElseThrow(() -> new AccountNotFoundException(
                        MessageFormat.format("The debit account with id: {0} does not exist!", event.getId())));

        var creditAccount = accountRepository.findById(event.getCreditAccountId())
                .orElseThrow(() -> new AccountNotFoundException(
                        MessageFormat.format("The credit account with id: {0} does not exist!", event.getId())));

        var transactionTime = LocalDateTime.now();
        debitAccount.setBalance(debitAccount.getBalance() - event.getAmount());
        debitAccount.setLastUpdatedDate(transactionTime);
        accountRepository.save(debitAccount);

        creditAccount.setBalance(creditAccount.getBalance()+ event.getAmount());
        creditAccount.setLastUpdatedDate(transactionTime);
        accountRepository.save(creditAccount);

        var transactionEvent = TransferTransactionEvent.builder()
                .id(debitAccount.getId())
                .debitAccountName(debitAccount.getName())
                .creditAccountId(creditAccount.getId())
                .creditAccountName(creditAccount.getName())
                .amount(event.getAmount())
                .description(MessageFormat.format("An amount of {0} was transferred from ({1}) account with id: {2} into ({3}) account with id: {4}",
                        event.getAmount(), debitAccount.getName(), debitAccount.getId(), creditAccount.getName(), creditAccount.getId()))
                .transactionTime(transactionTime)
                .build();
        transactionEventProducer.produce("TransferTransactionEvent", transactionEvent);
    }


    @Override
    public void on(AccountClosedEvent event) {
        accountRepository.deleteById(event.getId());
    }
}
