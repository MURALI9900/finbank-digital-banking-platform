package com.finbank.transaction.service;

import com.finbank.transaction.dto.*;
import com.finbank.transaction.entity.*;
import com.finbank.transaction.event.TransactionEvent;
import com.finbank.transaction.exception.*;
import com.finbank.transaction.repository.BankTransactionRepository;
import com.finbank.transaction.repository.TransactionOutboxRepository;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.client.RestClient;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {
    private static final String TOPIC="finbank.transaction.events";
    private final BankTransactionRepository repository;
    private final KafkaTemplate<String,TransactionEvent> kafkaTemplate;
    private final RestClient accountClient;
    private final TransactionOutboxRepository outboxRepository;
    private final String internalServiceToken;

    public TransactionServiceImpl(BankTransactionRepository repository,KafkaTemplate<String,TransactionEvent> kafkaTemplate,
                                  RestClient.Builder restClientBuilder, TransactionOutboxRepository outboxRepository,
                                  @org.springframework.beans.factory.annotation.Value("${finbank.services.account-url:http://localhost:8082}") String accountServiceUrl,
                                  @org.springframework.beans.factory.annotation.Value("${finbank.internal.service-token:dev-internal-token}") String internalServiceToken){
        this.repository=repository;
        this.kafkaTemplate=kafkaTemplate;
        this.outboxRepository=outboxRepository;
        this.internalServiceToken=internalServiceToken;
        this.accountClient=restClientBuilder.baseUrl(accountServiceUrl).build();
    }

    @Override @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request){
        if(repository.findByIdempotencyKey(request.idempotencyKey()).isPresent())
            throw new DuplicateTransactionException("Transaction already exists for idempotency key");
        validateTransaction(request);
        BankTransaction transaction=new BankTransaction();
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
        BankTransaction saved;
        try {
            saved=repository.saveAndFlush(transaction);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateTransactionException("Transaction already exists for idempotency key");
        }
        try{
            accountClient.post().uri("/api/v1/accounts/internal/balance-transaction")
                    .header("X-Service-Token", internalServiceToken)
                    .contentType(MediaType.APPLICATION_JSON).body(toBalanceRequest(saved)).retrieve().toBodilessEntity();
            saved.setStatus(TransactionStatus.SUCCESS);
        }catch(Exception ex){
            saved.setStatus(TransactionStatus.FAILED);
            repository.save(saved);
            throw new InvalidTransactionException("Account balance operation failed: "+rootMessage(ex));
        }
        BankTransaction completed=repository.save(saved);
        TransactionOutbox outbox = new TransactionOutbox();
        outbox.setTransactionReference(completed.getTransactionReference());
        outbox.setCustomerNumber(completed.getCustomerNumber());
        outbox.setSourceAccountNumber(completed.getSourceAccountNumber());
        outbox.setDestinationAccountNumber(completed.getDestinationAccountNumber());
        outbox.setType(completed.getType().name());
        outbox.setAmount(completed.getAmount());
        outbox.setCurrency(completed.getCurrency());
        outbox.setDescription(completed.getDescription());
        outboxRepository.save(outbox);
        return toResponse(completed);
    }

    @Override @Transactional(readOnly=true)
    public TransactionResponse getTransaction(String transactionReference){
        return toResponse(repository.findByTransactionReference(transactionReference)
                .orElseThrow(()->new TransactionNotFoundException("Transaction not found")));
    }

    @Override @Transactional(readOnly=true)
    public List<TransactionResponse> getCustomerTransactions(String customerNumber){
        return repository.findByCustomerNumberOrderByCreatedAtDesc(customerNumber.trim().toUpperCase()).stream().map(this::toResponse).toList();
    }

    private BalanceTransactionPayload toBalanceRequest(BankTransaction t){
        return new BalanceTransactionPayload(t.getTransactionReference(),t.getType().name(),t.getSourceAccountNumber(),
                t.getDestinationAccountNumber(),t.getAmount(),t.getCurrency());
    }

    private void validateTransaction(CreateTransactionRequest request){
        if(request.amount().compareTo(BigDecimal.ZERO)<=0) throw new InvalidTransactionException("Amount must be greater than zero");
        if(request.type()==TransactionType.DEPOSIT&&request.destinationAccountNumber()==null) throw new InvalidTransactionException("Destination account is required for deposit");
        if(request.type()==TransactionType.WITHDRAWAL&&request.sourceAccountNumber()==null) throw new InvalidTransactionException("Source account is required for withdrawal");
        if(request.type()==TransactionType.TRANSFER&&(request.sourceAccountNumber()==null||request.destinationAccountNumber()==null)) throw new InvalidTransactionException("Source and destination accounts are required for transfer");
        if(request.type()==TransactionType.TRANSFER&&request.sourceAccountNumber().trim().equalsIgnoreCase(request.destinationAccountNumber().trim())) throw new InvalidTransactionException("Source and destination accounts must be different");
    }
    private String generateReference(){return "FT"+UUID.randomUUID().toString().replace("-","").substring(0,16).toUpperCase();}
    private String normalize(String value){return value==null||value.isBlank()?null:value.trim();}
    private String rootMessage(Exception ex){return ex.getMessage()==null?"unknown account error":ex.getMessage();}
    private TransactionResponse toResponse(BankTransaction t){return new TransactionResponse(t.getTransactionReference(),t.getIdempotencyKey(),t.getCustomerNumber(),t.getSourceAccountNumber(),t.getDestinationAccountNumber(),t.getType(),t.getStatus(),t.getAmount(),t.getCurrency(),t.getDescription(),t.getCreatedAt(),t.getUpdatedAt());}
    private record BalanceTransactionPayload(String transactionReference,String type,String sourceAccountNumber,String destinationAccountNumber,BigDecimal amount,String currency){}
}