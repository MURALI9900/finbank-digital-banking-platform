package com.finbank.loan.controller;

import com.finbank.loan.dto.*;
import com.finbank.loan.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {
    private final LoanService service;
    public LoanController(LoanService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse apply(@Valid @RequestBody CreateLoanRequest r, Authentication auth) {
        requireCustomerOwnership(r.customerNumber(), auth);
        return service.apply(r);
    }

    @GetMapping("/{reference}")
    public LoanResponse get(@PathVariable String reference, Authentication auth) {
        LoanResponse loan = service.get(reference);
        requireCustomerOwnership(loan.customerNumber(), auth);
        return loan;
    }

    @GetMapping("/customer/{customerNumber}")
    public List<LoanResponse> customer(@PathVariable String customerNumber, Authentication auth) {
        requireCustomerOwnership(customerNumber, auth);
        return service.customerLoans(customerNumber);
    }

    @PutMapping("/{reference}/decision")
    public LoanResponse decide(@PathVariable String reference, @Valid @RequestBody LoanDecisionRequest r, Authentication auth) {
        requireOfficerOrAdmin(auth);
        return service.decide(reference, r);
    }

    @PostMapping("/{reference}/disburse")
    public LoanResponse disburse(@PathVariable String reference, @Valid @RequestBody DisbursementRequest r, Authentication auth) {
        requireOfficerOrAdmin(auth);
        return service.disburse(reference, r);
    }

    @PostMapping("/repayments")
    @ResponseStatus(HttpStatus.CREATED)
    public RepaymentResponse repayment(@Valid @RequestBody RepaymentRequest r, Authentication auth) {
        LoanResponse loan = service.get(r.loanReference());
        requireCustomerOwnership(loan.customerNumber(), auth);
        return service.createRepayment(r);
    }

    @PostMapping("/repayments/{reference}/pay")
    public RepaymentResponse pay(@PathVariable String reference, @Valid @RequestBody PaymentRequest r, Authentication auth) {
        String loanReference = service.repaymentLoanReference(reference);
        LoanResponse loan = service.get(loanReference);
        requireCustomerOwnership(loan.customerNumber(), auth);
        return service.pay(reference, r);
    }

    @GetMapping("/{reference}/repayments")
    public List<RepaymentResponse> repayments(@PathVariable String reference, Authentication auth) {
        LoanResponse loan = service.get(reference);
        requireCustomerOwnership(loan.customerNumber(), auth);
        return service.repayments(reference);
    }

    private void requireOfficerOrAdmin(Authentication auth) {
        if (auth == null || auth.getAuthorities().stream().noneMatch(a ->
                "ROLE_OFFICER".equals(a.getAuthority()) || "ROLE_ADMIN".equals(a.getAuthority()))) {
            throw new AccessDeniedException("Officer or admin role required");
        }
    }

    private void requireCustomerOwnership(String customerNumber, Authentication auth) {
        if (auth == null) throw new AccessDeniedException("Authentication required");
        boolean elevated = auth.getAuthorities().stream().anyMatch(a ->
                "ROLE_OFFICER".equals(a.getAuthority()) || "ROLE_ADMIN".equals(a.getAuthority()));
        if (elevated) return;
        String principal = auth.getName();
        String tokenCustomer = principal != null && principal.contains("|")
                ? principal.substring(principal.indexOf('|') + 1) : "";
        if (!customerNumber.equalsIgnoreCase(tokenCustomer)) {
            throw new AccessDeniedException("Customer ownership violation");
        }
    }
}