package com.finbank.officer.dto;

import com.finbank.officer.entity.OfficerStatus;

import java.time.LocalDateTime;

public record OfficerResponse(
        String officerCode,
        String fullName,
        String email,
        String branchCode,
        OfficerStatus status,
        LocalDateTime createdAt
) {}