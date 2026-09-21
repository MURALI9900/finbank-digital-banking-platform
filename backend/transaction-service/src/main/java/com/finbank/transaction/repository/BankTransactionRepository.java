package com.finbank.transaction.repository;

import com.finbank.transaction.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    Optional<BankTransaction> findByTransactionReference(String transactionReference);
    Optional<BankTransaction> findByIdempotencyKey(String idempotencyKey);
    List<BankTransaction> findByCustomerNumberOrderByCreatedAtDesc(String customerNumber);
}