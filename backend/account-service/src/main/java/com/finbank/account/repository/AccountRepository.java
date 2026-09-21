package com.finbank.account.repository;

import com.finbank.account.entity.Account;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomerNumber(String customerNumber);

    boolean existsByAccountNumber(String accountNumber);
}