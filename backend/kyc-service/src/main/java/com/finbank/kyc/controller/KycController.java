package com.finbank.kyc.controller;

import com.finbank.kyc.dto.*;
import com.finbank.kyc.service.KycService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kyc")
public class KycController {
    private final KycService kycService;

    public KycController(KycService kycService) {
        this.kycService = kycService;
    }

    @PostMapping("/applications")
    @ResponseStatus(HttpStatus.CREATED)
    public KycResponse create(@Valid @RequestBody CreateKycRequest request) {
        return kycService.createApplication(request);
    }

    @PostMapping("/applications/{reference}/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public KycDocumentResponse addDocument(@PathVariable String reference, @Valid @RequestBody AddDocumentRequest request) {
        return kycService.addDocument(reference, request);
    }

    @PostMapping("/applications/{reference}/submit")
    public KycResponse submit(@PathVariable String reference) {
        return kycService.submitApplication(reference);
    }

    @PutMapping("/applications/{reference}/review")
    public KycResponse review(@PathVariable String reference, @Valid @RequestBody ReviewKycRequest request) {
        return kycService.reviewApplication(reference, request);
    }

    @GetMapping("/applications/{reference}")
    public KycResponse get(@PathVariable String reference) {
        return kycService.getByApplicationReference(reference);
    }

    @GetMapping("/customers/{customerNumber}")
    public KycResponse getLatest(@PathVariable String customerNumber) {
        return kycService.getLatestByCustomer(customerNumber);
    }

    @GetMapping("/applications/{reference}/documents")
    public List<KycDocumentResponse> documents(@PathVariable String reference) {
        return kycService.getDocuments(reference);
    }
}