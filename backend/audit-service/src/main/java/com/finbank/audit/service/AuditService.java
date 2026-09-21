package com.finbank.audit.service;

import com.finbank.audit.dto.AuditResponse;
import com.finbank.audit.dto.CreateAuditRequest;
import com.finbank.audit.entity.AuditResult;

import java.util.List;

public interface AuditService {
    AuditResponse create(CreateAuditRequest request);
    AuditResponse get(String reference);
    List<AuditResponse> byCustomer(String customerNumber);
    List<AuditResponse> byActor(String actorId);
    List<AuditResponse> byEntity(String entityReference);
    List<AuditResponse> byResult(AuditResult result);
}