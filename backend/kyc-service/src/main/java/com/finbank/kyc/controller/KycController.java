package com.finbank.kyc.controller;

import com.finbank.kyc.dto.*;
import com.finbank.kyc.service.KycService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/kyc")
public class KycController {
    private final KycService kycService;
    public KycController(KycService kycService) { this.kycService = kycService; }

    @PostMapping("/applications")
    @ResponseStatus(HttpStatus.CREATED)
    public KycResponse create(@Valid @RequestBody CreateKycRequest request, Authentication authentication) {
        authorizeCustomer(authentication, request.customerNumber());
        return kycService.createApplication(request);
    }

    @PostMapping("/applications/{reference}/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public KycDocumentResponse addDocument(@PathVariable String reference, @Valid @RequestBody AddDocumentRequest request, Authentication authentication) {
        KycResponse application = kycService.getByApplicationReference(reference);
        authorizeCustomer(authentication, application.customerNumber());
        return kycService.addDocument(reference, request);
    }

    @PostMapping("/applications/{reference}/submit")
    public KycResponse submit(@PathVariable String reference, Authentication authentication) {
        KycResponse application = kycService.getByApplicationReference(reference);
        authorizeCustomer(authentication, application.customerNumber());
        return kycService.submitApplication(reference);
    }

    @PutMapping("/applications/{reference}/review")
    public KycResponse review(@PathVariable String reference, @Valid @RequestBody ReviewKycRequest request, Authentication authentication) {
        requireOfficerOrAdmin(authentication);
        return kycService.reviewApplication(reference, request);
    }

    @GetMapping("/applications/{reference}")
    public KycResponse get(@PathVariable String reference, Authentication authentication) {
        KycResponse application = kycService.getByApplicationReference(reference);
        authorizeCustomer(authentication, application.customerNumber());
        return application;
    }

    @GetMapping("/customers/{customerNumber}")
    public KycResponse getLatest(@PathVariable String customerNumber, Authentication authentication) {
        authorizeCustomer(authentication, customerNumber);
        return kycService.getLatestByCustomer(customerNumber);
    }

    @GetMapping("/applications/{reference}/documents")
    public List<KycDocumentResponse> documents(@PathVariable String reference, Authentication authentication) {
        KycResponse application = kycService.getByApplicationReference(reference);
        authorizeCustomer(authentication, application.customerNumber());
        return kycService.getDocuments(reference);
    }

    private void authorizeCustomer(Authentication authentication, String resourceCustomerNumber) {
        if (isCustomer(authentication) && !customerNumber(authentication).equalsIgnoreCase(resourceCustomerNumber)) {
            throw new AccessDeniedException("Customer can only access their own KYC data");
        }
    }

    private void requireOfficerOrAdmin(Authentication authentication) {
        if (authentication == null || (!hasRole(authentication, "OFFICER") && !hasRole(authentication, "ADMIN"))) {
            throw new AccessDeniedException("Only officers and admins can review KYC applications");
        }
    }

    private boolean isCustomer(Authentication authentication) { return hasRole(authentication, "CUSTOMER"); }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication != null && authentication.getAuthorities().stream()
            .anyMatch(a -> ("ROLE_" + role).equals(a.getAuthority()));
    }

    private String customerNumber(Authentication authentication) {
        String principal = authentication.getName();
        int separator = principal.indexOf('|');
        return separator >= 0 ? principal.substring(separator + 1) : "";
    }
}