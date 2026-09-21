package com.finbank.loan.dto;
import jakarta.validation.constraints.*; import java.math.BigDecimal;
public record RepaymentRequest(@NotBlank String loanReference,@NotNull @Min(1) Integer installmentNumber,@NotNull @FutureOrPresent java.time.LocalDate dueDate,@NotNull @DecimalMin("0.01") BigDecimal dueAmount){}