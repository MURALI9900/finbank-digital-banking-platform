package com.finbank.officer.repository;

import com.finbank.officer.entity.OfficerReview;
import com.finbank.officer.entity.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfficerReviewRepository extends JpaRepository<OfficerReview, Long> {
    Optional<OfficerReview> findByReviewReference(String reviewReference);
    List<OfficerReview> findByCustomerNumberOrderByCreatedAtDesc(String customerNumber);
    List<OfficerReview> findByStatusOrderByCreatedAtDesc(ReviewStatus status);
}