package com.finbank.loan.entity;
import jakarta.persistence.*; import lombok.Getter; import lombok.Setter; import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime;
@Entity @Table(name="loan_repayments",indexes={@Index(name="idx_repay_loan",columnList="loanReference"),@Index(name="idx_repay_status",columnList="status")}) @Getter @Setter
public class LoanRepayment{
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
@Column(nullable=false,unique=true,length=30) private String repaymentReference;
@Column(nullable=false,length=30) private String loanReference;
@Column(nullable=false) private Integer installmentNumber;
@Column(nullable=false) private LocalDate dueDate;
@Column(nullable=false,precision=19,scale=2) private BigDecimal dueAmount;
@Column(nullable=false,precision=19,scale=2) private BigDecimal paidAmount;
@Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private RepaymentStatus status;
private LocalDate paidDate; @Column(nullable=false) private LocalDateTime createdAt;
@PrePersist void prePersist(){createdAt=LocalDateTime.now();if(paidAmount==null)paidAmount=BigDecimal.ZERO;if(status==null)status=RepaymentStatus.PENDING;}
}