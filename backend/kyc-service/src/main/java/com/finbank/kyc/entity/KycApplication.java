package com.finbank.kyc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_applications", indexes = {
    @Index(name = "idx_kyc_customer", columnList = "customerNumber"),
    @Index(name = "idx_kyc_status", columnList = "status")
})
@Getter
@Setter
public class KycApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String applicationReference;

    @Column(nullable = false, length = 30)
    private String customerNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private KycStatus status;

    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;

    @Column(length = 30)
    private String reviewedBy;

    @Column(length = 500)
    private String rejectionReason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) status = KycStatus.NOT_SUBMITTED;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}