package com.finbank.officer.dto;

import com.finbank.officer.entity.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DecisionRequest(
        @NotNull ReviewStatus status,
        @Size(max = 500) String remarks
) {}