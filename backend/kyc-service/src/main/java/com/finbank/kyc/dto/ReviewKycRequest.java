package com.finbank.kyc.dto;

import com.finbank.kyc.entity.KycStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewKycRequest(
        @NotNull KycStatus status,
        @NotBlank @Size(max=30) String reviewedBy,
        @Size(max=500) String rejectionReason
) {}