package com.finbank.customer.controller;

import com.finbank.customer.config.AuthenticatedUser;
import com.finbank.customer.dto.CreateCustomerRequest;
import com.finbank.customer.dto.CustomerResponse;
import com.finbank.customer.service.CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request));
    }

    @GetMapping("/{customerNumber}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable String customerNumber, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (isCustomer(user) && !customerNumber.equalsIgnoreCase(user.customerNumber())) {
            throw new AccessDeniedException("Customer can only access their own profile");
        }
        return ResponseEntity.ok(customerService.getCustomerByNumber(customerNumber));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (isCustomer(user)) {
            throw new AccessDeniedException("Customer cannot access the customer directory");
        }
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    private boolean isCustomer(AuthenticatedUser user) {
        return "CUSTOMER".equalsIgnoreCase(user.role());
    }
}
