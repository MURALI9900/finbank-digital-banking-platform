package com.finbank.audit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_events", indexes = {
    @Index(name = "idx_audit_actor", columnList = "actor_id"),
    @Index(name = "idx_audit_customer", columnList = "customer_number"),
    @Index(name = "idx_audit_reference", columnList = "entity_reference"),
    @Index(name = "idx_audit_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "audit_reference", nullable = false, unique = true, length = 40)
    private String auditReference;

    @Column(name = "actor_id", length = 100)
    private String actorId;

    @Column(name = "actor_role", length = 30)
    private String actorRole;

    @Column(name = "customer_number", length = 30)
    private String customerNumber;

    @Column(name = "action", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @Column(name = "result", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AuditResult result;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_reference", length = 100)
    private String entityReference;

    @Column(length = 2000)
    private String description;

    @Column(name = "source_ip", length = 45)
    private String sourceIp;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}