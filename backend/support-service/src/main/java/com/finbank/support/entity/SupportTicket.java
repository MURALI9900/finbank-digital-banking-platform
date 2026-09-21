package com.finbank.support.entity;
import jakarta.persistence.*;import lombok.Getter;import lombok.Setter;import java.time.LocalDateTime;
@Entity @Table(name="support_tickets",indexes={@Index(name="idx_support_customer",columnList="customerNumber"),@Index(name="idx_support_status",columnList="status")}) @Getter @Setter
public class SupportTicket{
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
@Column(nullable=false,unique=true,length=30) private String ticketReference;
@Column(nullable=false,length=30) private String customerNumber;
@Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private RequestType requestType;
@Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private SupportStatus status;
@Enumerated(EnumType.STRING) @Column(nullable=false,length=10) private Priority priority;
@Column(nullable=false,length=150) private String subject;
@Column(nullable=false,length=2000) private String description;
@Column(length=30) private String assignedOfficer;
@Column(length=2000) private String resolution;
@Column(nullable=false) private LocalDateTime createdAt;@Column(nullable=false) private LocalDateTime updatedAt;
@PrePersist void prePersist(){LocalDateTime n=LocalDateTime.now();createdAt=n;updatedAt=n;if(status==null)status=SupportStatus.OPEN;if(priority==null)priority=Priority.MEDIUM;}
@PreUpdate void preUpdate(){updatedAt=LocalDateTime.now();}
}