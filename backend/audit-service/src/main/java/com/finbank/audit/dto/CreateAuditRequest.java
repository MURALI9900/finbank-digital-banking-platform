package com.finbank.audit.dto;

import com.finbank.audit.entity.AuditAction;
import com.finbank.audit.entity.AuditResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAuditRequest {
    @Size(max = 100)
    private String actorId;

    @Size(max = 30)
    private String actorRole;

    @Size(max = 30)
    private String customerNumber;

    @NotNull
    private AuditAction action;

    @NotNull
    private AuditResult result;

    @NotBlank
    @Size(max = 50)
    private String entityType;

    @Size(max = 100)
    private String entityReference;

    @NotBlank
    @Size(max = 2000)
    private String description;

    @Size(max = 45)
    private String sourceIp;
}