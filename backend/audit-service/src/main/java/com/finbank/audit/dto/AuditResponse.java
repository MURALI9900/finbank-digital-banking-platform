package com.finbank.audit.dto;

import com.finbank.audit.entity.AuditAction;
import com.finbank.audit.entity.AuditResult;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuditResponse {
    private String auditReference;
    private String actorId;
    private String actorRole;
    private String customerNumber;
    private AuditAction action;
    private AuditResult result;
    private String entityType;
    private String entityReference;
    private String description;
    private String sourceIp;
    private LocalDateTime createdAt;
}