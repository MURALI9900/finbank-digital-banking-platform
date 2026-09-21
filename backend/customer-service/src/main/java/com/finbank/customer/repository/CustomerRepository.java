package com.finbank.customer.repository;

import com.finbank.customer.entity.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerNumber(String customerNumber);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByMobileNumber(String mobileNumber);

    boolean existsByCustomerNumber(String customerNumber);

    boolean existsByEmail(String email);

    boolean existsByMobileNumber(String mobileNumber);
}
