package com.finbank.officer.repository;

import com.finbank.officer.entity.Officer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfficerRepository extends JpaRepository<Officer, Long> {
    Optional<Officer> findByOfficerCode(String officerCode);
    boolean existsByOfficerCode(String officerCode);
    boolean existsByEmail(String email);
}