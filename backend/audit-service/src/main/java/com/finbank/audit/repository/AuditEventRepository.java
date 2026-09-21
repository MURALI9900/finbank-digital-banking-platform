package com.finbank.audit.repository;

import com.finbank.audit.entity.AuditEvent;
import com.finbank.audit.entity.AuditResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {\n    Optional<AuditEvent> findByAuditReference(String auditReference);
    List<AuditEvent> findByCustomerNumberOrderByCreatedAtDesc(String customerNumber);
    List<AuditEvent> findByActorIdOrderByCreatedAtDesc(String actorId);
    List<AuditEvent> findByEntityReferenceOrderByCreatedAtDesc(String entityReference);
    List<AuditEvent> findByResultOrderByCreatedAtDesc(AuditResult result);
}