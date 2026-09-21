package com.finbank.support.dto;
import com.finbank.support.entity.*;import jakarta.validation.constraints.*;
public record CreateTicketRequest(@NotBlank String customerNumber,@NotNull RequestType requestType,@NotNull Priority priority,@NotBlank @Size(max=150) String subject,@NotBlank @Size(max=2000) String description){}