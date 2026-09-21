package com.finbank.beneficiary.dto;

import com.finbank.beneficiary.entity.BeneficiaryStatus;

import java.time.LocalDateTime;

public record BeneficiaryResponse(
        String beneficiaryReference,
        String customerNumber,
        String beneficiaryName,
        String beneficiaryAccountNumber,
        String bankName,
        String bankCode,
        String nickname,
        BeneficiaryStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}