package com.finbank.transaction.service;

import com.finbank.transaction.dto.CreateTransactionRequest;
import com.finbank.transaction.dto.TransactionResponse;
import com.finbank.transaction.entity.BankTransaction;
import com.finbank.transaction.entity.TransactionStatus;
import com.finbank.transaction.entity.TransactionType;
import com.finbank.transaction.event.TransactionEvent;
import com.finbank.transaction.exception.DuplicateTransactionException;
import com.finbank.transaction.exception.InvalidTransactionException;
import com.finbank.transaction.exception.TransactionNotFoundException;
import com.finbank.transaction.repository.BankTransactionRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {
    private static final String TOPIC = "finbank.transaction.events";
    private final BankTransactionRepository repository;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public TransactionServiceImpl(BankTransactionRepository repository, KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        if (repository.findByIdempotencyKey(request.idempotencyKey()).isPresent()) {
            throw new DuplicateTransactionException("Transaction already exists for idempotency key");
        }
        validateTransaction(request);
        BankTransaction transaction = new BankTransaction();
        transaction.setTransactionReference(generateReference());
        transaction.setIdempotencyKey(request.idempotencyKey().trim());
        transaction.setCustomerNumber(request.customerNumber().trim().toUpperCase());
        transaction.setSourceAccountNumber(normalize(request.sourceAccountNumber()));
        transaction.setDestinationAccountNumber(normalize(request.destinationAccountNumber()));
        transaction.setType(request.type());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAmount(request.amount().setScale(2));
        transaction.setCurrency(request.currency().trim().toUpperCase());
        transaction.setDescription(normalize(request.description()));
        BankTransaction saved = repository.save(transaction);
        kafkaTemplate.send(TOPIC, saved.getTransactionReference(),
                new TransactionEvent(saved.getTransactionReference(), saved.getCustomerNumber(),
                        saved.getSourceAccountNumber(), saved.getDestinationAccountNumber(), saved.getType(),
                        saved.getAmount(), saved.getCurrency(), saved.getDescription()));
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(String transactionReference) {
        return toResponse(repository.findByTransactionReference(transactionReference)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getCustomerTransactions(String customerNumber) {
        return repository.findByCustomerNumberOrderByCreatedAtDesc(customerNumber.trim().toUpperCase())
                .stream().map(this::toResponse).toList();
    }

    private void validateTransaction(CreateTransactionRequest request) {
        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) throw new InvalidTransactionException("Amount must be greater than zero");
        if (request.type() == TransactionType.DEPOSIT && request.destinationAccountNumber() == null) throw new InvalidTransactionException("Destination account is required for deposit");
        if (request.type() == TransactionType.WITHDRAWAL && request.sourceAccountNumber() == null) throw new InvalidTransactionException("Source account is required for withdrawal");
        if (request.type() == TransactionType.TRANSFER && (request.sourceAccountNumber() == null || request.destinationAccountNumber() == null)) throw new InvalidTransactionException("Source and destination accounts are required for transfer");
        if (request.type() == TransactionType.TRANSFER && request.sourceAccountNumber().trim().equalsIgnoreCase(request.destinationAccountNumber().trim())) throw new InvalidTransactionException("Source and destination accounts must be different");
    }

    private String generateReference() { return "FT" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase(); }
    private String normalize(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private TransactionResponse toResponse(BankTransaction t) {
        return new TransactionResponse(t.getTransactionReference(), t.getIdempotencyKey(), t.getCustomerNumber(),
                t.getSourceAccountNumber(), t.getDestinationAccountNumber(), t.getType(), t.getStatus(),
                t.getAmount(), t.getCurrency(), t.getDescription(), t.getCreatedAt(), t.getUpdatedAt());
    }
}