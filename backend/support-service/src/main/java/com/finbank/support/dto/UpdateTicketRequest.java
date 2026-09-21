package com.finbank.support.dto;
import com.finbank.support.entity.*;import jakarta.validation.constraints.NotNull;
public record UpdateTicketRequest(@NotNull SupportStatus status,String assignedOfficer,String resolution){}