package com.finbank.transaction.service;

import com.finbank.transaction.dto.CreateTransactionRequest;
import com.finbank.transaction.entity.TransactionType;
import com.finbank.transaction.exception.DuplicateTransactionException;
import com.finbank.transaction.repository.BankTransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private BankTransactionRepository repository;

    @InjectMocks
    private TransactionServiceImpl service;

    @Test
    void shouldRejectDuplicateIdempotencyKey() {
        when(repository.findByIdempotencyKey("KEY-1")).thenReturn(Optional.of(new com.finbank.transaction.entity.BankTransaction()));

        CreateTransactionRequest request = new CreateTransactionRequest(
                "FB100", "FBACC1", "FBACC2", TransactionType.TRANSFER,
                new BigDecimal("100.00"), "INR", "Test transfer", "KEY-1");

        assertThrows(DuplicateTransactionException.class, () -> service.createTransaction(request));
    }

    @Test
    void shouldCreateTransaction() {
        when(repository.findByIdempotencyKey("KEY-2")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CreateTransactionRequest request = new CreateTransactionRequest(
                "FB100", null, "FBACC1", TransactionType.DEPOSIT,
                new BigDecimal("500.00"), "INR", "Cash deposit", "KEY-2");

        var response = service.createTransaction(request);

        org.junit.jupiter.api.Assertions.assertEquals(TransactionType.DEPOSIT, response.type());
        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("500.00"), response.amount());
    }
}