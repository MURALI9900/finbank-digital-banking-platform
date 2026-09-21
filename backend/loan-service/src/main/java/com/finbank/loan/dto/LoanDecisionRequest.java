package com.finbank.loan.dto;

import com.finbank.loan.entity.LoanStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record LoanDecisionRequest(
        @NotNull LoanStatus status,
        @NotBlank String reviewedBy,
        @DecimalMin("0.00") BigDecimal approvedInterestRate,
        String rejectionReason
) {}