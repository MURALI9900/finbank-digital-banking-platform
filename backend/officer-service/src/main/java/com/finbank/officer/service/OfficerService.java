package com.finbank.officer.service;

import com.finbank.officer.dto.*;

import java.util.List;

public interface OfficerService {
    OfficerResponse createOfficer(CreateOfficerRequest request);
    OfficerResponse getOfficer(String officerCode);
    ReviewResponse createReview(CreateReviewRequest request);
    ReviewResponse decideReview(String reviewReference, DecisionRequest request);
    ReviewResponse getReview(String reviewReference);
    List<ReviewResponse> getCustomerReviews(String customerNumber);
    List<ReviewResponse> getPendingReviews();
}