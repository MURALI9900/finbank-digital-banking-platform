package com.finbank.officer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "officer_reviews", indexes = {
        @Index(name = "idx_review_customer", columnList = "customer_number"),
        @Index(name = "idx_review_type", columnList = "review_type")
})
@Getter
@Setter
@NoArgsConstructor
public class OfficerReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "review_reference", nullable = false, unique = true, length = 30)
    private String reviewReference;

    @Column(name = "customer_number", nullable = false, length = 20)
    private String customerNumber;

    @Column(name = "review_type", nullable = false, length = 40)
    private String reviewType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReviewStatus status;

    @Column(length = 500)
    private String remarks;

    @Column(name = "officer_code", nullable = false, length = 30)
    private String officerCode;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) status = ReviewStatus.PENDING;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}