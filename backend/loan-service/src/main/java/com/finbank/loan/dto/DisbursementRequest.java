package com.finbank.loan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DisbursementRequest(
        @NotBlank @jakarta.validation.constraints.Size(max = 30) String destinationAccountNumber,
        @NotBlank @Pattern(regexp = "^[A-Z]{3}$") String currency
) {}
