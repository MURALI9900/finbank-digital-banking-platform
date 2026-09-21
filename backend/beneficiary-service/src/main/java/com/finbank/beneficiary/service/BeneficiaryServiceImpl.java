package com.finbank.beneficiary.service;

import com.finbank.beneficiary.dto.BeneficiaryResponse;
import com.finbank.beneficiary.dto.CreateBeneficiaryRequest;
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

    public BeneficiaryServiceImpl(BeneficiaryRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public BeneficiaryResponse createBeneficiary(CreateBeneficiaryRequest request) {
        String customerNumber = request.customerNumber().trim();
        String accountNumber = request.beneficiaryAccountNumber().trim();

        if (repository.existsByCustomerNumberAndBeneficiaryAccountNumber(customerNumber, accountNumber)) {
            throw new DuplicateBeneficiaryException("Beneficiary already exists for this account");
        }

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setBeneficiaryReference(generateReference());
        beneficiary.setCustomerNumber(customerNumber);
        beneficiary.setBeneficiaryName(request.beneficiaryName().trim());
        beneficiary.setBeneficiaryAccountNumber(accountNumber);
        beneficiary.setBankName(request.bankName().trim());
        beneficiary.setBankCode(normalize(request.bankCode()));
        beneficiary.setNickname(normalize(request.nickname()));
        beneficiary.setStatus(BeneficiaryStatus.PENDING);

        return toResponse(repository.save(beneficiary));
    }

    @Override
    @Transactional(readOnly = true)
    public BeneficiaryResponse getBeneficiary(String reference) {
        return toResponse(repository.findByBeneficiaryReference(reference)
                .orElseThrow(() -> new BeneficiaryNotFoundException("Beneficiary not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BeneficiaryResponse> getCustomerBeneficiaries(String customerNumber) {
        return repository.findByCustomerNumberOrderByCreatedAtDesc(customerNumber.trim())
                .stream().map(this::toResponse).toList();
    }

    private String generateReference() {
        return "FBEN" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private BeneficiaryResponse toResponse(Beneficiary b) {
        return new BeneficiaryResponse(
                b.getBeneficiaryReference(), b.getCustomerNumber(), b.getBeneficiaryName(),
                b.getBeneficiaryAccountNumber(), b.getBankName(), b.getBankCode(),
                b.getNickname(), b.getStatus(), b.getCreatedAt(), b.getUpdatedAt());
    }
}