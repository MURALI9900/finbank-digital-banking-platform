package com.finbank.officer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateReviewRequest(
        @NotBlank @Size(max = 20) String customerNumber,
        @NotBlank @Size(max = 40) String reviewType,
        @Size(max = 30) String targetReference,
        @Size(max = 500) String remarks,
        @NotBlank @Size(max = 30) String officerCode
) {}