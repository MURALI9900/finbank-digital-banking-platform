package com.finbank.transaction.dto;

import com.finbank.transaction.entity.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateTransactionRequest(
        @NotBlank @Size(max = 20) String customerNumber,
        @Size(max = 30) String sourceAccountNumber,
        @Size(max = 30) String destinationAccountNumber,
        @NotNull TransactionType type,
        @NotNull @DecimalMin(value = "0.01") @Digits(integer = 17, fraction = 2) BigDecimal amount,
        @NotBlank @Pattern(regexp = "^[A-Z]{3}$") String currency,
        @Size(max = 255) String description,
        @NotBlank @Size(max = 100) String idempotencyKey
) {}