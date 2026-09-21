package com.finbank.beneficiary.service;

import com.finbank.beneficiary.dto.*;
import com.finbank.beneficiary.entity.Beneficiary;
import com.finbank.beneficiary.entity.BeneficiaryStatus;
import com.finbank.beneficiary.exception.BeneficiaryNotFoundException;
import com.finbank.beneficiary.exception.DuplicateBeneficiaryException;
import com.finbank.beneficiary.repository.BeneficiaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class BeneficiaryServiceImpl implements BeneficiaryService {
    private final BeneficiaryRepository repository;
    public BeneficiaryServiceImpl(BeneficiaryRepository repository){this.repository=repository;}

    @Override @Transactional
    public BeneficiaryResponse createBeneficiary(CreateBeneficiaryRequest request){
        String customerNumber=request.customerNumber().trim().toUpperCase();
        String accountNumber=request.beneficiaryAccountNumber().trim().toUpperCase();
        if(repository.existsByCustomerNumberAndBeneficiaryAccountNumber(customerNumber,accountNumber))
            throw new DuplicateBeneficiaryException("Beneficiary already exists for this account");
        Beneficiary b=new Beneficiary();
        b.setBeneficiaryReference(generateReference());
        b.setCustomerNumber(customerNumber);
        b.setBeneficiaryName(request.beneficiaryName().trim());
        b.setBeneficiaryAccountNumber(accountNumber);
        b.setBankName(request.bankName().trim());
        b.setBankCode(normalize(request.bankCode()));
        b.setNickname(normalize(request.nickname()));
        b.setStatus(BeneficiaryStatus.PENDING);
        return toResponse(repository.save(b));
    }

    @Override @Transactional(readOnly=true)
    public BeneficiaryResponse getBeneficiary(String reference){
        return toResponse(repository.findByBeneficiaryReference(reference)
                .orElseThrow(()->new BeneficiaryNotFoundException("Beneficiary not found")));
    }

    @Override @Transactional(readOnly=true)
    public List<BeneficiaryResponse> getCustomerBeneficiaries(String customerNumber){
        return repository.findByCustomerNumberOrderByCreatedAtDesc(customerNumber.trim().toUpperCase())
                .stream().map(this::toResponse).toList();
    }

    @Override @Transactional
    public BeneficiaryResponse decideBeneficiary(String reference,BeneficiaryDecisionRequest request){
        Beneficiary b=repository.findByBeneficiaryReference(reference)
                .orElseThrow(()->new BeneficiaryNotFoundException("Beneficiary not found"));
        if(b.getStatus()!=BeneficiaryStatus.PENDING) throw new IllegalStateException("Only pending beneficiaries can be decided");
        if(request.status()==BeneficiaryStatus.PENDING) throw new IllegalArgumentException("Decision must be APPROVED or REJECTED");
        if(request.status()==BeneficiaryStatus.REJECTED && (request.remarks()==null||request.remarks().isBlank()))
            throw new IllegalArgumentException("Remarks are required when rejecting a beneficiary");
        b.setStatus(request.status());
        return toResponse(repository.save(b));
    }

    private String generateReference(){return "FBEN"+UUID.randomUUID().toString().replace("-","").substring(0,16).toUpperCase();}
    private String normalize(String value){return value==null||value.isBlank()?null:value.trim();}
    private BeneficiaryResponse toResponse(Beneficiary b){return new BeneficiaryResponse(b.getBeneficiaryReference(),b.getCustomerNumber(),b.getBeneficiaryName(),b.getBeneficiaryAccountNumber(),b.getBankName(),b.getBankCode(),b.getNickname(),b.getStatus(),b.getCreatedAt(),b.getUpdatedAt());}
}