package com.finbank.beneficiary.repository;

import com.finbank.beneficiary.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    Optional<Beneficiary> findByBeneficiaryReference(String beneficiaryReference);
    List<Beneficiary> findByCustomerNumberOrderByCreatedAtDesc(String customerNumber);
    boolean existsByCustomerNumberAndBeneficiaryAccountNumber(String customerNumber, String beneficiaryAccountNumber);
}