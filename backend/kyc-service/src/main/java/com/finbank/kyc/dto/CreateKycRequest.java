package com.finbank.kyc.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateKycRequest(
    @NotBlank String customerNumber
) {}