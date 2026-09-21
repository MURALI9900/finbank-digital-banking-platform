package com.finbank.support.dto;
import com.finbank.support.entity.*;import java.time.LocalDateTime;
public record TicketResponse(String ticketReference,String customerNumber,RequestType requestType,SupportStatus status,Priority priority,String subject,String description,String assignedOfficer,String resolution,LocalDateTime createdAt,LocalDateTime updatedAt){}