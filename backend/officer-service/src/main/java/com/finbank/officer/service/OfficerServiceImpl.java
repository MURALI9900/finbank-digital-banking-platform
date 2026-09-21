package com.finbank.officer.service;

import com.finbank.officer.dto.*;
import com.finbank.officer.entity.*;
import com.finbank.officer.exception.OfficerException;
import com.finbank.officer.repository.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.UUID;

@Service
public class OfficerServiceImpl implements OfficerService {
    private final OfficerRepository officerRepository;
    private final OfficerReviewRepository reviewRepository;
    private final RestClient beneficiaryClient;
    private final RestClient kycClient;

    public OfficerServiceImpl(OfficerRepository officerRepository,OfficerReviewRepository reviewRepository,RestClient.Builder builder, @org.springframework.beans.factory.annotation.Value("${finbank.services.beneficiary-url:http://localhost:8084}") String beneficiaryServiceUrl, @org.springframework.beans.factory.annotation.Value("${finbank.services.kyc-url:http://localhost:8087}") String kycServiceUrl){
        this.officerRepository=officerRepository; this.reviewRepository=reviewRepository;
        this.beneficiaryClient=builder.baseUrl(beneficiaryServiceUrl).build();
        this.kycClient=builder.baseUrl(kycServiceUrl).build();
    }

    @Override @Transactional
    public OfficerResponse createOfficer(CreateOfficerRequest request){
        String email=request.email().trim().toLowerCase();
        if(officerRepository.existsByEmail(email)) throw new OfficerException("Officer email already exists");
        Officer o=new Officer(); o.setOfficerCode(generateOfficerCode()); o.setFullName(request.fullName().trim());
        o.setEmail(email); o.setBranchCode(request.branchCode().trim().toUpperCase()); o.setStatus(OfficerStatus.ACTIVE);
        return toResponse(officerRepository.save(o));
    }

    @Override @Transactional(readOnly=true)
    public OfficerResponse getOfficer(String officerCode){return toResponse(officerRepository.findByOfficerCode(officerCode).orElseThrow(()->new OfficerException("Officer not found")));}

    @Override @Transactional
    public ReviewResponse createReview(CreateReviewRequest request){
        officerRepository.findByOfficerCode(request.officerCode().trim()).orElseThrow(()->new OfficerException("Officer not found"));
        if((request.reviewType().equalsIgnoreCase("KYC")||request.reviewType().equalsIgnoreCase("BENEFICIARY"))
                &&(request.targetReference()==null||request.targetReference().isBlank()))
            throw new OfficerException("Target reference is required for KYC and beneficiary reviews");
        OfficerReview r=new OfficerReview(); r.setReviewReference(generateReviewReference());
        r.setCustomerNumber(request.customerNumber().trim().toUpperCase()); r.setReviewType(request.reviewType().trim().toUpperCase());
        r.setTargetReference(normalize(request.targetReference())); r.setRemarks(normalize(request.remarks()));
        r.setOfficerCode(request.officerCode().trim()); r.setStatus(ReviewStatus.PENDING);
        return toReviewResponse(reviewRepository.save(r));
    }

    @Override @Transactional
    public ReviewResponse decideReview(String reviewReference,DecisionRequest request){
        OfficerReview r=reviewRepository.findByReviewReference(reviewReference).orElseThrow(()->new OfficerException("Review not found"));
        if(r.getStatus()!=ReviewStatus.PENDING) throw new OfficerException("Only pending reviews can be decided");
        if(request.status()==ReviewStatus.PENDING) throw new OfficerException("Decision must be APPROVED or REJECTED");
        r.setStatus(request.status()); r.setRemarks(normalize(request.remarks()));
        ReviewResponse response=toReviewResponse(reviewRepository.save(r));
        try{
            if("BENEFICIARY".equalsIgnoreCase(r.getReviewType())&&r.getTargetReference()!=null)
                beneficiaryClient.put().uri("/api/v1/beneficiaries/{reference}/decision",r.getTargetReference())
                    .contentType(MediaType.APPLICATION_JSON).body(new Decision(request.status().name(),request.remarks())).retrieve().toBodilessEntity();
            if("KYC".equalsIgnoreCase(r.getReviewType())&&r.getTargetReference()!=null)
                kycClient.put().uri("/api/v1/kyc/applications/{reference}/review",r.getTargetReference())
                    .contentType(MediaType.APPLICATION_JSON).body(new KycDecision(request.status().name().equals("APPROVED")?"VERIFIED":"REJECTED",r.getOfficerCode(),request.remarks())).retrieve().toBodilessEntity();
        }catch(Exception ex){throw new OfficerException("Review target update failed: "+(ex.getMessage()==null?"unknown error":ex.getMessage()));}
        return response;
    }

    @Override @Transactional(readOnly=true)
    public ReviewResponse getReview(String ref){return toReviewResponse(reviewRepository.findByReviewReference(ref).orElseThrow(()->new OfficerException("Review not found")));}
    @Override @Transactional(readOnly=true)
    public List<ReviewResponse> getCustomerReviews(String customer){return reviewRepository.findByCustomerNumberOrderByCreatedAtDesc(customer.trim().toUpperCase()).stream().map(this::toReviewResponse).toList();}
    @Override @Transactional(readOnly=true)
    public List<ReviewResponse> getPendingReviews(){return reviewRepository.findByStatusOrderByCreatedAtDesc(ReviewStatus.PENDING).stream().map(this::toReviewResponse).toList();}
    private String generateOfficerCode(){return "OFF"+UUID.randomUUID().toString().replace("-","").substring(0,10).toUpperCase();}
    private String generateReviewReference(){return "REV"+UUID.randomUUID().toString().replace("-","").substring(0,14).toUpperCase();}
    private String normalize(String v){return v==null||v.isBlank()?null:v.trim();}
    private OfficerResponse toResponse(Officer o){return new OfficerResponse(o.getOfficerCode(),o.getFullName(),o.getEmail(),o.getBranchCode(),o.getStatus(),o.getCreatedAt());}
    private ReviewResponse toReviewResponse(OfficerReview r){return new ReviewResponse(r.getReviewReference(),r.getCustomerNumber(),r.getReviewType(),r.getTargetReference(),r.getStatus(),r.getRemarks(),r.getOfficerCode(),r.getCreatedAt(),r.getUpdatedAt());}
    private record Decision(String status,String remarks){}
    private record KycDecision(String status,String reviewedBy,String rejectionReason){}
}