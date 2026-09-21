package com.finbank.transaction.controller;

import com.finbank.transaction.dto.CreateTransactionRequest;
import com.finbank.transaction.dto.TransactionResponse;
import com.finbank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody CreateTransactionRequest request) {
        return service.createTransaction(request);
    }

    @GetMapping("/{transactionReference}")
    public TransactionResponse get(@PathVariable String transactionReference) {
        return service.getTransaction(transactionReference);
    }

    @GetMapping("/customer/{customerNumber}")
    public List<TransactionResponse> getCustomerTransactions(@PathVariable String customerNumber) {
        return service.getCustomerTransactions(customerNumber);
    }
}