package com.finbank.kyc.service;

import com.finbank.kyc.dto.*;
import com.finbank.kyc.entity.*;
import com.finbank.kyc.exception.KycException;
import com.finbank.kyc.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class KycServiceImpl implements KycService {
    private final KycApplicationRepository applicationRepository;
    private final KycDocumentRepository documentRepository;

    public KycServiceImpl(KycApplicationRepository applicationRepository, KycDocumentRepository documentRepository) {
        this.applicationRepository = applicationRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    @Transactional
    public KycResponse createApplication(CreateKycRequest request) {
        KycApplication application = new KycApplication();
        application.setApplicationReference("KYC" + UUID.randomUUID().toString().replace("-", "").substring(0, 18).toUpperCase());
        application.setCustomerNumber(request.customerNumber().trim().toUpperCase());
        application.setStatus(KycStatus.NOT_SUBMITTED);
        return toResponse(applicationRepository.save(application));
    }

    @Override
    @Transactional
    public KycDocumentResponse addDocument(String reference, AddDocumentRequest request) {
        KycApplication application = getEntity(reference);
        if (application.getStatus() != KycStatus.NOT_SUBMITTED) {
            throw new KycException("Documents can only be added before KYC submission");
        }
        KycDocument document = new KycDocument();
        document.setDocumentReference("KYCDOC" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        document.setApplicationReference(application.getApplicationReference());
        document.setDocumentType(request.documentType());
        document.setDocumentNumber(request.documentNumber().trim());
        document.setFileName(request.fileName());
        document.setStatus(DocumentStatus.PENDING);
        return toDocumentResponse(documentRepository.save(document));
    }

    @Override
    @Transactional
    public KycResponse submitApplication(String reference) {
        KycApplication application = getEntity(reference);
        if (application.getStatus() != KycStatus.NOT_SUBMITTED) {
            throw new KycException("KYC application is not in draft state");
        }
        if (documentRepository.findByApplicationReferenceOrderByCreatedAtAsc(reference).isEmpty()) {
            throw new KycException("At least one KYC document is required");
        }
        application.setStatus(KycStatus.PENDING);
        application.setSubmittedAt(java.time.LocalDateTime.now());
        return toResponse(applicationRepository.save(application));
    }

    @Override
    @Transactional
    public KycResponse reviewApplication(String reference, ReviewKycRequest request) {
        KycApplication application = getEntity(reference);
        if (application.getStatus() != KycStatus.PENDING) {
            throw new KycException("Only pending KYC applications can be reviewed");
        }
        if (request.status() != KycStatus.VERIFIED && request.status() != KycStatus.REJECTED) {
            throw new KycException("Review status must be VERIFIED or REJECTED");
        }
        if (request.status() == KycStatus.REJECTED && (request.rejectionReason() == null || request.rejectionReason().isBlank())) {
            throw new KycException("Rejection reason is required");
        }
        application.setStatus(request.status());
        application.setReviewedBy(request.reviewedBy().trim());
        application.setReviewedAt(java.time.LocalDateTime.now());
        application.setRejectionReason(request.rejectionReason());
        return toResponse(applicationRepository.save(application));
    }

    @Override
    public KycResponse getByApplicationReference(String reference) {
        return toResponse(getEntity(reference));
    }

    @Override
    public KycResponse getLatestByCustomer(String customerNumber) {
        return applicationRepository.findTopByCustomerNumberOrderByCreatedAtDesc(customerNumber.trim().toUpperCase())
            .map(this::toResponse)
            .orElseThrow(() -> new KycException("No KYC application found for customer"));
    }

    @Override
    public List<KycDocumentResponse> getDocuments(String reference) {
        getEntity(reference);
        return documentRepository.findByApplicationReferenceOrderByCreatedAtAsc(reference)
            .stream().map(this::toDocumentResponse).toList();
    }

    private KycApplication getEntity(String reference) {
        return applicationRepository.findByApplicationReference(reference)
            .orElseThrow(() -> new KycException("KYC application not found"));
    }

    private KycResponse toResponse(KycApplication application) {
        return new KycResponse(application.getApplicationReference(), application.getCustomerNumber(), application.getStatus(),
            application.getSubmittedAt(), application.getReviewedAt(), application.getReviewedBy(), application.getRejectionReason());
    }

    private KycDocumentResponse toDocumentResponse(KycDocument document) {
        return new KycDocumentResponse(document.getDocumentReference(), document.getApplicationReference(), document.getDocumentType(),
            document.getDocumentNumber(), document.getFileName(), document.getStatus());
    }
}