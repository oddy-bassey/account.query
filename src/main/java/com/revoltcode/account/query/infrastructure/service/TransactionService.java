package com.revoltcode.account.query.infrastructure.service;

import com.revoltcode.account.common.event.transaction.TransactionEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "feignTransactionService", url = "http://localhost:8080/api/v1/transactions")
public interface TransactionService {

    @PostMapping("/")
    ResponseEntity<String> postTransaction(@RequestBody TransactionEvent transaction);
}
