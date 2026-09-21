package com.finbank.loan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank @jakarta.validation.constraints.Size(max = 30) String sourceAccountNumber,
        @NotBlank @Pattern(regexp = "^[A-Z]{3}$") String currency
) {}
