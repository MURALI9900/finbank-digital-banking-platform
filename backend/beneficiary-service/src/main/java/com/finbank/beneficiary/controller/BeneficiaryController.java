package com.finbank.beneficiary.controller;

import com.finbank.beneficiary.dto.*;
import com.finbank.beneficiary.service.BeneficiaryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficiaries")
public class BeneficiaryController {
    private final BeneficiaryService service;\n    private final String internalServiceToken;
    public BeneficiaryController(BeneficiaryService service, @org.springframework.beans.factory.annotation.Value("${finbank.internal.service-token}") String internalServiceToken){this.service=service; this.internalServiceToken=internalServiceToken;}

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public BeneficiaryResponse create(@Valid @RequestBody CreateBeneficiaryRequest request, Authentication authentication){
        if(isCustomer(authentication) && !customer(authentication).equalsIgnoreCase(request.customerNumber().trim()))
            throw new AccessDeniedException("Customers can only create beneficiaries for themselves");
        return service.createBeneficiary(request);
    }

    @GetMapping("/{reference}")
    public BeneficiaryResponse get(@PathVariable String reference, Authentication authentication){
        BeneficiaryResponse response=service.getBeneficiary(reference);
        if(isCustomer(authentication) && !customer(authentication).equalsIgnoreCase(response.customerNumber()))
            throw new AccessDeniedException("Customers can only access their own beneficiaries");
        return response;
    }

    @GetMapping("/customer/{customerNumber}")
    public List<BeneficiaryResponse> getCustomerBeneficiaries(@PathVariable String customerNumber, Authentication authentication){
        if(isCustomer(authentication) && !customer(authentication).equalsIgnoreCase(customerNumber.trim()))
            throw new AccessDeniedException("Customers can only access their own beneficiaries");
        return service.getCustomerBeneficiaries(customerNumber);
    }

    @PutMapping("/internal/{reference}/decision")\n    public BeneficiaryResponse internalDecide(@PathVariable String reference, @RequestHeader(value = "X-Service-Token", required = false) String serviceToken, @Valid @RequestBody BeneficiaryDecisionRequest request){\n        if(serviceToken==null || !serviceToken.equals(internalServiceToken)) throw new AccessDeniedException("Internal service authentication required");\n        return service.decideBeneficiary(reference,request);\n    }\n\n    @PutMapping("/{reference}/decision")
    public BeneficiaryResponse decide(@PathVariable String reference,@Valid @RequestBody BeneficiaryDecisionRequest request, Authentication authentication){
        if(!hasOfficerRole(authentication)) throw new AccessDeniedException("Only officers can decide beneficiaries");
        return service.decideBeneficiary(reference,request);
    }

    private boolean isCustomer(Authentication authentication){return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));}
    private boolean hasOfficerRole(Authentication authentication){return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OFFICER")||a.getAuthority().equals("ROLE_ADMIN"));}
    private String customer(Authentication authentication){
        String principal=authentication.getName();
        int separator=principal.indexOf('|');
        return separator>=0 ? principal.substring(separator+1) : "";
    }
}