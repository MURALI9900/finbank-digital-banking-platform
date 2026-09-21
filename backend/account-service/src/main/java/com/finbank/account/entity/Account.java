package com.finbank.account.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="accounts")
public class Account {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="account_number",nullable=false,unique=true,length=20) private String accountNumber;
    @Column(name="customer_number",nullable=false,length=20) private String customerNumber;
    @Enumerated(EnumType.STRING) @Column(name="account_type",nullable=false,length=30) private AccountType accountType;
    @Enumerated(EnumType.STRING) @Column(name="status",nullable=false,length=20) private AccountStatus status;
    @Column(name="currency",nullable=false,length=3) private String currency;
    @Column(name="balance",nullable=false,precision=19,scale=4) private BigDecimal balance;
    @Column(name="available_balance",nullable=false,precision=19,scale=4) private BigDecimal availableBalance;
    @Column(name="created_at",nullable=false,updatable=false) private LocalDateTime createdAt;
    @Column(name="updated_at",nullable=false) private LocalDateTime updatedAt;
    @Version @Column(name="version",nullable=false) private Long version;
    @PrePersist protected void onCreate(){LocalDateTime now=LocalDateTime.now();createdAt=now;updatedAt=now;if(status==null)status=AccountStatus.PENDING;if(balance==null)balance=BigDecimal.ZERO;if(availableBalance==null)availableBalance=BigDecimal.ZERO;if(currency==null)currency="INR";}
    @PreUpdate protected void onUpdate(){updatedAt=LocalDateTime.now();}
    public Long getId(){return id;} public String getAccountNumber(){return accountNumber;} public void setAccountNumber(String v){accountNumber=v;}
    public String getCustomerNumber(){return customerNumber;} public void setCustomerNumber(String v){customerNumber=v;}
    public AccountType getAccountType(){return accountType;} public void setAccountType(AccountType v){accountType=v;}
    public AccountStatus getStatus(){return status;} public void setStatus(AccountStatus v){status=v;}
    public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;}
    public BigDecimal getBalance(){return balance;} public void setBalance(BigDecimal v){balance=v;}
    public BigDecimal getAvailableBalance(){return availableBalance;} public void setAvailableBalance(BigDecimal v){availableBalance=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;} public Long getVersion(){return version;}
}