package com.finbank.officer.service;

import com.finbank.officer.dto.*;
import com.finbank.officer.entity.*;
import com.finbank.officer.exception.OfficerException;
import com.finbank.officer.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OfficerServiceImpl implements OfficerService {
    private final OfficerRepository officerRepository;
    private final OfficerReviewRepository reviewRepository;

    public OfficerServiceImpl(OfficerRepository officerRepository, OfficerReviewRepository reviewRepository) {
        this.officerRepository = officerRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional
    public OfficerResponse createOfficer(CreateOfficerRequest request) {
        String email = request.email().trim().toLowerCase();
        if (officerRepository.existsByEmail(email)) throw new OfficerException("Officer email already exists");

        Officer officer = new Officer();
        officer.setOfficerCode(generateOfficerCode());
        officer.setFullName(request.fullName().trim());
        officer.setEmail(email);
        officer.setBranchCode(request.branchCode().trim().toUpperCase());
        officer.setStatus(OfficerStatus.ACTIVE);
        return toResponse(officerRepository.save(officer));
    }

    @Override
    @Transactional(readOnly = true)
    public OfficerResponse getOfficer(String officerCode) {
        return toResponse(officerRepository.findByOfficerCode(officerCode)
                .orElseThrow(() -> new OfficerException("Officer not found")));
    }

    @Override
    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        officerRepository.findByOfficerCode(request.officerCode().trim())
                .orElseThrow(() -> new OfficerException("Officer not found"));

        OfficerReview review = new OfficerReview();
        review.setReviewReference(generateReviewReference());
        review.setCustomerNumber(request.customerNumber().trim());
        review.setReviewType(request.reviewType().trim().toUpperCase());
        review.setRemarks(normalize(request.remarks()));
        review.setOfficerCode(request.officerCode().trim());
        review.setStatus(ReviewStatus.PENDING);
        return toReviewResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public ReviewResponse decideReview(String reviewReference, DecisionRequest request) {
        OfficerReview review = reviewRepository.findByReviewReference(reviewReference)
                .orElseThrow(() -> new OfficerException("Review not found"));

        if (review.getStatus() != ReviewStatus.PENDING) {
            throw new OfficerException("Only pending reviews can be decided");
        }
        if (request.status() == ReviewStatus.PENDING) {
            throw new OfficerException("Decision must be APPROVED or REJECTED");
        }

        review.setStatus(request.status());
        review.setRemarks(normalize(request.remarks()));
        return toReviewResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReview(String reviewReference) {
        return toReviewResponse(reviewRepository.findByReviewReference(reviewReference)
                .orElseThrow(() -> new OfficerException("Review not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getCustomerReviews(String customerNumber) {
        return reviewRepository.findByCustomerNumberOrderByCreatedAtDesc(customerNumber.trim())
                .stream().map(this::toReviewResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getPendingReviews() {
        return reviewRepository.findByStatusOrderByCreatedAtDesc(ReviewStatus.PENDING)
                .stream().map(this::toReviewResponse).toList();
    }

    private String generateOfficerCode() {
        return "OFF" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private String generateReviewReference() {
        return "REV" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase();
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private OfficerResponse toResponse(Officer officer) {
        return new OfficerResponse(officer.getOfficerCode(), officer.getFullName(), officer.getEmail(),
                officer.getBranchCode(), officer.getStatus(), officer.getCreatedAt());
    }

    private ReviewResponse toReviewResponse(OfficerReview review) {
        return new ReviewResponse(review.getReviewReference(), review.getCustomerNumber(),
                review.getReviewType(), review.getStatus(), review.getRemarks(), review.getOfficerCode(),
                review.getCreatedAt(), review.getUpdatedAt());
    }
}