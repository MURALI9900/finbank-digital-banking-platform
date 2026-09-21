package com.finbank.officer.dto;

import com.finbank.officer.entity.ReviewStatus;
import java.time.LocalDateTime;

public record ReviewResponse(
        String reviewReference,String customerNumber,String reviewType,String targetReference,
        ReviewStatus status,String remarks,String officerCode,LocalDateTime createdAt,LocalDateTime updatedAt) {}