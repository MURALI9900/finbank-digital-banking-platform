package com.finbank.transaction.event;

import com.finbank.transaction.entity.TransactionType;
import java.math.BigDecimal;

public record TransactionEvent(
        String transactionReference,
        String customerNumber,
        String sourceAccountNumber,
        String destinationAccountNumber,
        TransactionType type,
        BigDecimal amount,
        String currency,
        String description
) {}