package com.finbank.audit.event;

import java.math.BigDecimal;

public record TransactionEvent(
        String transactionReference,
        String customerNumber,
        String sourceAccountNumber,
        String destinationAccountNumber,
        String type,
        BigDecimal amount,
        String currency,
        String description
) {}