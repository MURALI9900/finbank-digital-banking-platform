package com.finbank.beneficiary.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "beneficiaries", indexes = {
        @Index(name = "idx_beneficiary_customer", columnList = "customer_number"),
        @Index(name = "idx_beneficiary_account", columnList = "beneficiary_account_number")
})
@Getter
@Setter
@NoArgsConstructor
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "beneficiary_reference", nullable = false, unique = true, length = 30)
    private String beneficiaryReference;

    @Column(name = "customer_number", nullable = false, length = 20)
    private String customerNumber;

    @Column(name = "beneficiary_name", nullable = false, length = 120)
    private String beneficiaryName;

    @Column(name = "beneficiary_account_number", nullable = false, length = 30)
    private String beneficiaryAccountNumber;

    @Column(name = "bank_name", nullable = false, length = 120)
    private String bankName;

    @Column(name = "bank_code", length = 30)
    private String bankCode;

    @Column(name = "nickname", length = 60)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BeneficiaryStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) status = BeneficiaryStatus.PENDING;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}