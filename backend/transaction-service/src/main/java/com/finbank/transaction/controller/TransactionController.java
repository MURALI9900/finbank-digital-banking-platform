package com.finbank.transaction.controller;

import com.finbank.transaction.dto.CreateTransactionRequest;
import com.finbank.transaction.dto.TransactionResponse;
import com.finbank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    private final TransactionService service;
    public TransactionController(TransactionService service){this.service=service;}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody CreateTransactionRequest request, Authentication authentication){
        if(isCustomer(authentication) && !customer(authentication).equalsIgnoreCase(request.customerNumber().trim()))
            throw new AccessDeniedException("Customers can only create transactions for themselves");
        return service.createTransaction(request);
    }

    @GetMapping("/{transactionReference}")
    public TransactionResponse get(@PathVariable String transactionReference, Authentication authentication){
        TransactionResponse response=service.getTransaction(transactionReference);
        if(isCustomer(authentication) && !customer(authentication).equalsIgnoreCase(response.customerNumber()))
            throw new AccessDeniedException("Customers can only access their own transactions");
        return response;
    }

    @GetMapping("/customer/{customerNumber}")
    public List<TransactionResponse> getCustomerTransactions(@PathVariable String customerNumber, Authentication authentication){
        if(isCustomer(authentication) && !customer(authentication).equalsIgnoreCase(customerNumber.trim()))
            throw new AccessDeniedException("Customers can only access their own transactions");
        return service.getCustomerTransactions(customerNumber);
    }

    private boolean isCustomer(Authentication authentication){return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));}
    private String customer(Authentication authentication){
        String principal=authentication.getName();
        int separator=principal.indexOf('|');
        return separator>=0 ? principal.substring(separator+1) : "";
    }
}