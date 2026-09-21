package com.finbank.transaction.service;

import com.finbank.transaction.entity.OutboxStatus;
import com.finbank.transaction.entity.TransactionOutbox;
import com.finbank.transaction.event.TransactionEvent;
import com.finbank.transaction.repository.TransactionOutboxRepository;
import java.time.LocalDateTime;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionOutboxPublisher {
    private static final String TOPIC = "finbank.transaction.events";

    private final TransactionOutboxRepository repository;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public TransactionOutboxPublisher(TransactionOutboxRepository repository,
                                       KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${finbank.outbox.poll-ms:5000}")
    @Transactional
    public void publishPending() {
        for (TransactionOutbox outbox : repository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING)) {
            TransactionEvent event = new TransactionEvent(
                    outbox.getTransactionReference(),
                    outbox.getCustomerNumber(),
                    outbox.getSourceAccountNumber(),
                    outbox.getDestinationAccountNumber(),
                    com.finbank.transaction.entity.TransactionType.valueOf(outbox.getType()),
                    outbox.getAmount(),
                    outbox.getCurrency(),
                    outbox.getDescription());

            try {
                kafkaTemplate.send(TOPIC, outbox.getTransactionReference(), event).get();
                outbox.setStatus(OutboxStatus.SENT);
                outbox.setSentAt(LocalDateTime.now());
                repository.save(outbox);
            } catch (Exception ignored) {
                // Keep the row PENDING so the next poll retries delivery.
            }
        }
    }
}