package com.finbank.officer.controller;

import com.finbank.officer.dto.*;
import com.finbank.officer.service.OfficerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/officers")
public class OfficerController {
    private final OfficerService service;

    public OfficerController(OfficerService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OfficerResponse createOfficer(@Valid @RequestBody CreateOfficerRequest request) {
        return service.createOfficer(request);
    }

    @GetMapping("/{officerCode}")
    public OfficerResponse getOfficer(@PathVariable String officerCode) {
        return service.getOfficer(officerCode);
    }

    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse createReview(@Valid @RequestBody CreateReviewRequest request) {
        return service.createReview(request);
    }

    @PutMapping("/reviews/{reviewReference}/decision")
    public ReviewResponse decide(@PathVariable String reviewReference, @Valid @RequestBody DecisionRequest request) {
        return service.decideReview(reviewReference, request);
    }

    @GetMapping("/reviews/{reviewReference}")
    public ReviewResponse getReview(@PathVariable String reviewReference) {
        return service.getReview(reviewReference);
    }

    @GetMapping("/reviews/customer/{customerNumber}")
    public List<ReviewResponse> customerReviews(@PathVariable String customerNumber) {
        return service.getCustomerReviews(customerNumber);
    }

    @GetMapping("/reviews/pending")
    public List<ReviewResponse> pendingReviews() {
        return service.getPendingReviews();
    }
}