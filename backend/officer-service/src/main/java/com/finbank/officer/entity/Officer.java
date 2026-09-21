package com.finbank.officer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bank_officers")
@Getter
@Setter
@NoArgsConstructor
public class Officer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "officer_code", nullable = false, unique = true, length = 30)
    private String officerCode;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false, length = 120)
    private String branchCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OfficerStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = OfficerStatus.ACTIVE;
    }
}