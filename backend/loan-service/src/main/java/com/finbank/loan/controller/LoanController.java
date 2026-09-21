package com.finbank.loan.controller;

import com.finbank.loan.dto.*;
import com.finbank.loan.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {
    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse apply(@Valid @RequestBody CreateLoanRequest r) {
        return service.apply(r);
    }

    @GetMapping("/{reference}")
    public LoanResponse get(@PathVariable String reference) {
        return service.get(reference);
    }

    @GetMapping("/customer/{customerNumber}")
    public List<LoanResponse> customer(@PathVariable String customerNumber) {
        return service.customerLoans(customerNumber);
    }

    @PutMapping("/{reference}/decision")
    public LoanResponse decide(@PathVariable String reference, @Valid @RequestBody LoanDecisionRequest r) {
        return service.decide(reference, r);
    }

    @PostMapping("/{reference}/disburse")
    public LoanResponse disburse(@PathVariable String reference, @Valid @RequestBody DisbursementRequest r) {
        return service.disburse(reference, r);
    }

    @PostMapping("/repayments")
    @ResponseStatus(HttpStatus.CREATED)
    public RepaymentResponse repayment(@Valid @RequestBody RepaymentRequest r) {
        return service.createRepayment(r);
    }

    @PostMapping("/repayments/{reference}/pay")
    public RepaymentResponse pay(@PathVariable String reference, @Valid @RequestBody PaymentRequest r) {
        return service.pay(reference, r);
    }

    @GetMapping("/{reference}/repayments")
    public List<RepaymentResponse> repayments(@PathVariable String reference) {
        return service.repayments(reference);
    }
}
