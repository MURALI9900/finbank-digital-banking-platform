package com.finbank.loan.entity;
import jakarta.persistence.*; import lombok.Getter; import lombok.Setter; import java.math.BigDecimal; import java.time.LocalDateTime;
@Entity @Table(name="loan_applications",indexes={@Index(name="idx_loan_customer",columnList="customerNumber"),@Index(name="idx_loan_status",columnList="status")}) @Getter @Setter
public class LoanApplication{
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
@Column(nullable=false,unique=true,length=30) private String applicationReference;
@Column(nullable=false,length=30) private String customerNumber;
@Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private LoanType loanType;
@Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private LoanStatus status;
@Column(nullable=false,precision=19,scale=2) private BigDecimal requestedAmount;
@Column(nullable=false) private Integer tenureMonths;
@Column(nullable=false,precision=7,scale=3) private BigDecimal annualInterestRate;
@Column(length=500) private String purpose;
@Column(length=30) private String reviewedBy;
@Column(length=500) private String rejectionReason;
private LocalDateTime submittedAt; private LocalDateTime reviewedAt; private LocalDateTime disbursedAt;
@Column(nullable=false) private LocalDateTime createdAt; @Column(nullable=false) private LocalDateTime updatedAt;
@PrePersist void prePersist(){LocalDateTime n=LocalDateTime.now();createdAt=n;updatedAt=n;if(status==null)status=LoanStatus.DRAFT;if(annualInterestRate==null)annualInterestRate=BigDecimal.ZERO;}
@PreUpdate void preUpdate(){updatedAt=LocalDateTime.now();}
}