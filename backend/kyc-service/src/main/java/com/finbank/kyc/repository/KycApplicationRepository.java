package com.finbank.kyc.repository;

import com.finbank.kyc.entity.KycApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycApplicationRepository extends JpaRepository<KycApplication, Long> {
    Optional<KycApplication> findByApplicationReference(String applicationReference);
    Optional<KycApplication> findTopByCustomerNumberOrderByCreatedAtDesc(String customerNumber);
}