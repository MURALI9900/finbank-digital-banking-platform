package com.finbank.loan.dto;

import com.finbank.transaction.entity.TransactionType;
import java.math.BigDecimal;

public record TransactionRequest(
        String customerNumber,
        String sourceAccountNumber,
        String destinationAccountNumber,
        TransactionType type,
        BigDecimal amount,
        String currency,
        String description,
        String idempotencyKey
) {}
