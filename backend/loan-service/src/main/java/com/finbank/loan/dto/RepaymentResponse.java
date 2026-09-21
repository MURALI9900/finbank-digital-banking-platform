package com.finbank.loan.dto;
import com.finbank.loan.entity.RepaymentStatus; import java.math.BigDecimal; import java.time.LocalDate;
public record RepaymentResponse(String repaymentReference,String loanReference,Integer installmentNumber,LocalDate dueDate,BigDecimal dueAmount,BigDecimal paidAmount,RepaymentStatus status,LocalDate paidDate){}