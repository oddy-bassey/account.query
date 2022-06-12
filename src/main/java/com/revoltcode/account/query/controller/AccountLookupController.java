package com.revoltcode.account.query.controller;

import com.revoltcode.account.query.domain.model.BankAccount;
import com.revoltcode.account.query.dto.AccountLookupResponse;
import com.revoltcode.account.query.query.*;
import com.revoltcode.cqrs.core.infrastructure.dispatcher.QueryDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/accountLookup")
public class AccountLookupController {

    private final Logger logger = Logger.getLogger(AccountLookupController.class.getName());

    @Autowired
    private final QueryDispatcher queryDispatcher;

    @GetMapping("/")
    public ResponseEntity<AccountLookupResponse> getAllAccounts(){
        try{
            List<BankAccount> accounts = queryDispatcher.send(new FindAllAccountQuery());
            if(accounts == null || accounts.size() <= 0) return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);

            var response = AccountLookupResponse.builder()
                    .accounts(accounts)
                    .message(MessageFormat.format("Successfully returned {0} bank account(s)", accounts.size()))
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            var safeErrorMessage = "Failed to complete get all accounts request!";
            logger.log(Level.SEVERE, safeErrorMessage, e);
            return new ResponseEntity<>(new AccountLookupResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byId/{id}")
    public ResponseEntity<AccountLookupResponse> getAccountById(@PathVariable("id") String id){
        try{
            List<BankAccount> accounts = queryDispatcher.send(new FindAccountByIdQuery(id));
            if(accounts == null || accounts.size() <= 0) return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);

            var response = AccountLookupResponse.builder()
                    .accounts(accounts)
                    .message("Successfully returned bank account!")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            var safeErrorMessage = "Failed to complete get account by id request!";
            logger.log(Level.SEVERE, safeErrorMessage, e);
            return new ResponseEntity<>(new AccountLookupResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byHolder/{accountHolder}")
    public ResponseEntity<AccountLookupResponse> getAccountByHolder(@PathVariable("accountHolder") String accountHolder){
        try{
            List<BankAccount> accounts = queryDispatcher.send(new FindAccountByCustomerIdQuery(accountHolder));
            if(accounts == null || accounts.size() <= 0) return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);

            var response = AccountLookupResponse.builder()
                    .accounts(accounts)
                    .message("Successfully returned bank account!")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            var safeErrorMessage = "Failed to complete get account by holder request!";
            logger.log(Level.SEVERE, safeErrorMessage, e);
            return new ResponseEntity<>(new AccountLookupResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
