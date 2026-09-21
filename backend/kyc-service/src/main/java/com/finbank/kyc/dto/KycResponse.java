package com.finbank.kyc.dto;

import com.finbank.kyc.entity.KycStatus;

import java.time.LocalDateTime;

public record KycResponse(
    String applicationReference,
    String customerNumber,
    KycStatus status,
    LocalDateTime submittedAt,
    LocalDateTime reviewedAt,
    String reviewedBy,
    String rejectionReason
) {}