package com.finbank.beneficiary.dto;

import com.finbank.beneficiary.entity.BeneficiaryStatus;
import jakarta.validation.constraints.NotNull;

public record BeneficiaryDecisionRequest(@NotNull BeneficiaryStatus status, String remarks) {}