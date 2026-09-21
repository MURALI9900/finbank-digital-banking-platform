package com.finbank.transaction.service;

import com.finbank.transaction.entity.OutboxStatus;
import com.finbank.transaction.entity.TransactionOutbox;
import com.finbank.transaction.entity.TransactionType;
import com.finbank.transaction.event.TransactionEvent;
import com.finbank.transaction.repository.TransactionOutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransactionOutboxPublisherTest {

    @Test
    void publishesPendingEventAndMarksItSent() {
        TransactionOutboxRepository repository = mock(TransactionOutboxRepository.class);
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, TransactionEvent> kafkaTemplate = mock(KafkaTemplate.class);

        TransactionOutbox outbox = new TransactionOutbox();
        outbox.setTransactionReference("FT123");
        outbox.setCustomerNumber("C001");
        outbox.setType(TransactionType.TRANSFER.name());
        outbox.setAmount(new BigDecimal("100.00"));
        outbox.setCurrency("USD");
        when(repository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(List.of(outbox));
        when(kafkaTemplate.send(anyString(), anyString(), any(TransactionEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        new TransactionOutboxPublisher(repository, kafkaTemplate).publishPending();

        verify(kafkaTemplate).send(eq("finbank.transaction.events"), eq("FT123"), any(TransactionEvent.class));
        assert outbox.getStatus() == OutboxStatus.SENT;
        verify(repository).save(outbox);
    }

    @Test
    void keepsEventPendingWhenKafkaPublishFails() {
        TransactionOutboxRepository repository = mock(TransactionOutboxRepository.class);
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, TransactionEvent> kafkaTemplate = mock(KafkaTemplate.class);

        TransactionOutbox outbox = new TransactionOutbox();
        outbox.setStatus(OutboxStatus.PENDING);
        outbox.setTransactionReference("FT124");
        outbox.setCustomerNumber("C001");
        outbox.setType(TransactionType.DEPOSIT.name());
        outbox.setAmount(new BigDecimal("50.00"));
        outbox.setCurrency("USD");
        when(repository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(List.of(outbox));
        when(kafkaTemplate.send(anyString(), anyString(), any(TransactionEvent.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka unavailable")));

        new TransactionOutboxPublisher(repository, kafkaTemplate).publishPending();

        verify(kafkaTemplate).send(eq("finbank.transaction.events"), eq("FT124"), any(TransactionEvent.class));
        assert outbox.getStatus() == OutboxStatus.PENDING;
        verify(repository, never()).save(outbox);
    }
}