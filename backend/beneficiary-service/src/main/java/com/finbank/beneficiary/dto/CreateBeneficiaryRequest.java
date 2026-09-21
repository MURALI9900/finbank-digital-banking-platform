package com.finbank.beneficiary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBeneficiaryRequest(
        @NotBlank @Size(max = 20) String customerNumber,
        @NotBlank @Size(max = 120) String beneficiaryName,
        @NotBlank @Size(max = 30) String beneficiaryAccountNumber,
        @NotBlank @Size(max = 120) String bankName,
        @Size(max = 30) String bankCode,
        @Size(max = 60) String nickname
) {}