package com.finbank.audit.event;

import com.finbank.audit.dto.CreateAuditRequest;
import com.finbank.audit.entity.AuditAction;
import com.finbank.audit.entity.AuditResult;
import com.finbank.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionEventListener {
    private final AuditService auditService;

    @KafkaListener(topics = "finbank.transaction.events", groupId = "audit-service", containerFactory = "kafkaListenerContainerFactory")
    public void handle(TransactionEvent event) {
        CreateAuditRequest request = new CreateAuditRequest();
        request.setActorId("transaction-service");
        request.setActorRole("SYSTEM");
        request.setCustomerNumber(event.customerNumber());
        request.setAction(toAction(event.type()));
        request.setResult(AuditResult.SUCCESS);
        request.setEntityType("TRANSACTION");
        request.setEntityReference(event.transactionReference());
        request.setDescription("Transaction event received from transaction-service");
        auditService.create(request);
    }

    private AuditAction toAction(String type) {
        return switch (type) {
            case "DEPOSIT" -> AuditAction.DEPOSIT;
            case "WITHDRAWAL" -> AuditAction.WITHDRAWAL;
            case "TRANSFER" -> AuditAction.TRANSFER;
            case "BILL_PAYMENT" -> AuditAction.UPDATE;
            default -> AuditAction.OTHER;
        };
    }
}