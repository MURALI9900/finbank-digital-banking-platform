package com.finbank.transaction.repository;

import com.finbank.transaction.entity.OutboxStatus;
import com.finbank.transaction.entity.TransactionOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionOutboxRepository extends JpaRepository<TransactionOutbox, Long> {
    List<TransactionOutbox> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}