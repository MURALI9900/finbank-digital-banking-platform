package com.finbank.transaction.service;

import com.finbank.transaction.dto.CreateTransactionRequest;
import com.finbank.transaction.entity.BankTransaction;
import com.finbank.transaction.entity.TransactionType;
import com.finbank.transaction.exception.DuplicateTransactionException;
import com.finbank.transaction.repository.BankTransactionRepository;
import com.finbank.transaction.repository.TransactionOutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {
    @Mock private BankTransactionRepository repository;
    @Mock private TransactionOutboxRepository outboxRepository;
    @Mock private KafkaTemplate<String, com.finbank.transaction.event.TransactionEvent> kafkaTemplate;
    @Mock private RestClient.Builder restClientBuilder;
    @Mock private RestClient accountClient;
    @Mock private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private RestClient.RequestBodySpec requestBodySpec;
    @Mock private RestClient.ResponseSpec responseSpec;
    private TransactionServiceImpl service;

    @BeforeEach
    void setUp() {
        when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(accountClient);
        service = new TransactionServiceImpl(
                repository, kafkaTemplate, restClientBuilder, outboxRepository,
                "http://localhost:8082", "dev-internal-token");
    }

    @Test
    void shouldRejectDuplicateIdempotencyKey() {
        when(repository.findByIdempotencyKey("KEY-1")).thenReturn(Optional.of(new BankTransaction()));
        CreateTransactionRequest request = new CreateTransactionRequest(
                "FB100", "FBACC1", "FBACC2", TransactionType.TRANSFER,
                new BigDecimal("100.00"), "INR", "Test transfer", "KEY-1");
        assertThrows(DuplicateTransactionException.class, () -> service.createTransaction(request));
    }

    @Test
    void shouldCreateTransaction() {
        when(repository.findByIdempotencyKey("KEY-2")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(outboxRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(accountClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/api/v1/accounts/internal/balance-transaction")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Object.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        CreateTransactionRequest request = new CreateTransactionRequest(
                "FB100", null, "FBACC1", TransactionType.DEPOSIT,
                new BigDecimal("500.00"), "INR", "Cash deposit", "KEY-2");

        var response = service.createTransaction(request);

        assertEquals(TransactionType.DEPOSIT, response.type());
        assertEquals(new BigDecimal("500.00"), response.amount());
    }
}
