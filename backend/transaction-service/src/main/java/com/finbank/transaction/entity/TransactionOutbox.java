package com.finbank.transaction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_outbox", indexes = {@Index(name = "idx_outbox_status", columnList = "status")})
@Getter @Setter @NoArgsConstructor
public class TransactionOutbox {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 30) private String transactionReference;
    @Column(nullable = false, length = 20) private String customerNumber;
    @Column(length = 30) private String sourceAccountNumber;
    @Column(length = 30) private String destinationAccountNumber;
    @Column(nullable = false, length = 30) private String type;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal amount;
    @Column(nullable = false, length = 3) private String currency;
    @Column(length = 255) private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private OutboxStatus status;
    @Column(nullable = false) private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    @PrePersist void onCreate(){createdAt=LocalDateTime.now();if(status==null)status=OutboxStatus.PENDING;}
}