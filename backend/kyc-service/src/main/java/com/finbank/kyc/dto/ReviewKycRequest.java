package com.finbank.kyc.dto;

import com.finbank.kyc.entity.KycStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewKycRequest(
    @NotNull KycStatus status,
    @NotBlank String reviewedBy,
    String rejectionReason
) {}