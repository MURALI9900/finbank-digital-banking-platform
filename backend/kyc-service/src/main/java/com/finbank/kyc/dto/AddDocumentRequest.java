package com.finbank.kyc.dto;

import com.finbank.kyc.entity.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddDocumentRequest(
    @NotNull DocumentType documentType,
    @NotBlank String documentNumber,
    String fileName
) {}