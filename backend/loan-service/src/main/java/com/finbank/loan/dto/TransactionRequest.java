package com.finbank.loan.dto;

import java.math.BigDecimal;

public record TransactionRequest(
        String customerNumber,
        String sourceAccountNumber,
        String destinationAccountNumber,
        String type,
        BigDecimal amount,
        String currency,
        String description,
        String idempotencyKey
) {}
