package com.finbank.kyc.dto;

import com.finbank.kyc.entity.DocumentStatus;
import com.finbank.kyc.entity.DocumentType;

public record KycDocumentResponse(
    String documentReference,
    String applicationReference,
    DocumentType documentType,
    String documentNumber,
    String fileName,
    DocumentStatus status
) {}