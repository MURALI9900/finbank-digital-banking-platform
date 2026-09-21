package com.finbank.audit.service;

import com.finbank.audit.dto.*;
import com.finbank.audit.entity.AuditEvent;
import com.finbank.audit.entity.AuditResult;
import com.finbank.audit.exception.AuditNotFoundException;
import com.finbank.audit.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditServiceImpl implements AuditService {
    private final AuditEventRepository repository;

    @Override
    public AuditResponse create(CreateAuditRequest request) {
        AuditEvent event = AuditEvent.builder()
                .auditReference("FAUD" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase())
                .actorId(normalize(request.getActorId()))
                .actorRole(normalize(request.getActorRole()))
                .customerNumber(normalizeUpper(request.getCustomerNumber()))
                .action(request.getAction())
                .result(request.getResult())
                .entityType(request.getEntityType().trim().toUpperCase())
                .entityReference(normalize(request.getEntityReference()))
                .description(request.getDescription().trim())
                .sourceIp(normalize(request.getSourceIp()))
                .build();
        return toResponse(repository.save(event));
    }

    @Override @Transactional(readOnly = true)
    public AuditResponse get(String reference) {
        return toResponse(repository.findByAuditReference(reference)
                .orElseThrow(() -> new AuditNotFoundException("Audit event not found: " + reference)));
    }

    @Override @Transactional(readOnly = true)
    public List<AuditResponse> byCustomer(String customerNumber) {
        return repository.findByCustomerNumberOrderByCreatedAtDesc(normalizeUpper(customerNumber)).stream().map(this::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<AuditResponse> byActor(String actorId) {
        return repository.findByActorIdOrderByCreatedAtDesc(normalize(actorId)).stream().map(this::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<AuditResponse> byEntity(String entityReference) {
        return repository.findByEntityReferenceOrderByCreatedAtDesc(normalize(entityReference)).stream().map(this::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<AuditResponse> byResult(AuditResult result) {
        return repository.findByResultOrderByCreatedAtDesc(result).stream().map(this::toResponse).toList();
    }

    private String normalize(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private String normalizeUpper(String value) { return value == null || value.isBlank() ? null : value.trim().toUpperCase(); }

    private AuditResponse toResponse(AuditEvent e) {
        return AuditResponse.builder()
                .auditReference(e.getAuditReference()).actorId(e.getActorId()).actorRole(e.getActorRole())
                .customerNumber(e.getCustomerNumber()).action(e.getAction()).result(e.getResult())
                .entityType(e.getEntityType()).entityReference(e.getEntityReference()).description(e.getDescription())
                .sourceIp(e.getSourceIp()).createdAt(e.getCreatedAt()).build();
    }
}