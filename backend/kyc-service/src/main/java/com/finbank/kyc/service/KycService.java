package com.finbank.kyc.service;

import com.finbank.kyc.dto.*;

import java.util.List;

public interface KycService {
    KycResponse createApplication(CreateKycRequest request);
    KycDocumentResponse addDocument(String applicationReference, AddDocumentRequest request);
    KycResponse submitApplication(String applicationReference);
    KycResponse reviewApplication(String applicationReference, ReviewKycRequest request);
    KycResponse getByApplicationReference(String applicationReference);
    KycResponse getLatestByCustomer(String customerNumber);
    List<KycDocumentResponse> getDocuments(String applicationReference);
}