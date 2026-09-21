package com.finbank.beneficiary.controller;

import com.finbank.beneficiary.dto.*;
import com.finbank.beneficiary.service.BeneficiaryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficiaries")
public class BeneficiaryController {
    private final BeneficiaryService service;
    public BeneficiaryController(BeneficiaryService service){this.service=service;}
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public BeneficiaryResponse create(@Valid @RequestBody CreateBeneficiaryRequest request){return service.createBeneficiary(request);}
    @GetMapping("/{reference}")
    public BeneficiaryResponse get(@PathVariable String reference){return service.getBeneficiary(reference);}
    @GetMapping("/customer/{customerNumber}")
    public List<BeneficiaryResponse> getCustomerBeneficiaries(@PathVariable String customerNumber){return service.getCustomerBeneficiaries(customerNumber);}
    @PutMapping("/{reference}/decision")
    public BeneficiaryResponse decide(@PathVariable String reference,@Valid @RequestBody BeneficiaryDecisionRequest request){return service.decideBeneficiary(reference,request);}
}