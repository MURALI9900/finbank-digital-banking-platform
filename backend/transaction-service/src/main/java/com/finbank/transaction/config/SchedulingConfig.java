package com.finbank.transaction.config;

import com.finbank.transaction.entity.OutboxStatus;
import com.finbank.transaction.entity.TransactionOutbox;
import com.finbank.transaction.entity.TransactionType;
import com.finbank.transaction.event.TransactionEvent;
import com.finbank.transaction.repository.TransactionOutboxRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    private static final String TOPIC = "finbank.transaction.events";
    private final TransactionOutboxRepository repository;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public SchedulingConfig(TransactionOutboxRepository repository, KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${finbank.outbox.publish-delay-ms:5000}")
    public void publishPending() {
        for (TransactionOutbox event : repository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING)) {
            try {
                kafkaTemplate.send(TOPIC, event.getTransactionReference(), new TransactionEvent(
                        event.getTransactionReference(), event.getCustomerNumber(),
                        event.getSourceAccountNumber(), event.getDestinationAccountNumber(),
                        TransactionType.valueOf(event.getType()), event.getAmount(),
                        event.getCurrency(), event.getDescription())).get();
                event.setStatus(OutboxStatus.SENT);
                event.setSentAt(LocalDateTime.now());
                repository.save(event);
            } catch (Exception ignored) {
            }
        }
    }
}