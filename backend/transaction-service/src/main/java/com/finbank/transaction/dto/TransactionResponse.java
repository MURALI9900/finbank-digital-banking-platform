package com.finbank.transaction.dto;

import com.finbank.transaction.entity.TransactionStatus;
import com.finbank.transaction.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        String transactionReference,
        String idempotencyKey,
        String customerNumber,
        String sourceAccountNumber,
        String destinationAccountNumber,
        TransactionType type,
        TransactionStatus status,
        BigDecimal amount,
        String currency,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}