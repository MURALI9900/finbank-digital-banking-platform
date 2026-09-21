package com.finbank.kyc.repository;

import com.finbank.kyc.entity.KycDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KycDocumentRepository extends JpaRepository<KycDocument, Long> {
    List<KycDocument> findByApplicationReferenceOrderByCreatedAtAsc(String applicationReference);
}