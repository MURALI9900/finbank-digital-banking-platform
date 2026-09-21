package com.finbank.account.controller;

import com.finbank.account.dto.*;
import com.finbank.account.service.AccountService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;
    private final String internalServiceToken;
    public AccountController(AccountService accountService, @org.springframework.beans.factory.annotation.Value("${finbank.internal.service-token}") String internalServiceToken){this.accountService=accountService; this.internalServiceToken=internalServiceToken;}

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request, Authentication authentication){
        if(isCustomer(authentication) && !customer(authentication).equalsIgnoreCase(request.getCustomerNumber().trim()))
            throw new AccessDeniedException("Customers can only create accounts for themselves");
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNumber, Authentication authentication){
        AccountResponse response=accountService.getAccount(accountNumber);
        if(isCustomer(authentication) && !customer(authentication).equalsIgnoreCase(response.getCustomerNumber()))
            throw new AccessDeniedException("Customers can only access their own accounts");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerNumber}")
    public ResponseEntity<List<AccountResponse>> getCustomerAccounts(@PathVariable String customerNumber, Authentication authentication){
        if(isCustomer(authentication) && !customer(authentication).equalsIgnoreCase(customerNumber.trim()))
            throw new AccessDeniedException("Customers can only access their own accounts");
        return ResponseEntity.ok(accountService.getCustomerAccounts(customerNumber));
    }

    @PostMapping("/internal/balance-transaction")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void applyBalanceTransaction(@RequestHeader(value = "X-Service-Token", required = false) String serviceToken, @Valid @RequestBody BalanceTransactionRequest request){\n        if(serviceToken==null || !serviceToken.equals(internalServiceToken)) throw new AccessDeniedException("Internal service authentication required");\n        accountService.applyBalanceTransaction(request);\n    }

    private boolean isCustomer(Authentication authentication){return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));}
    private String customer(Authentication authentication){
        String principal=authentication.getName();
        int separator=principal.indexOf('|');
        return separator >= 0 ? principal.substring(separator+1) : "";
    }
}