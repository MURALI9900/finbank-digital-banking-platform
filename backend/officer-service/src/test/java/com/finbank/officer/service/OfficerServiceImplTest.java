package com.finbank.officer.service;

import com.finbank.officer.dto.CreateReviewRequest;
import com.finbank.officer.entity.Officer;
import com.finbank.officer.entity.OfficerStatus;
import com.finbank.officer.repository.OfficerRepository;
import com.finbank.officer.repository.OfficerReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfficerServiceImplTest {
    @Mock OfficerRepository officerRepository;
    @Mock OfficerReviewRepository reviewRepository;
    @InjectMocks OfficerServiceImpl service;

    @Test
    void shouldCreateReview() {
        Officer officer = new Officer();
        officer.setOfficerCode("OFF001");
        officer.setStatus(OfficerStatus.ACTIVE);
        when(officerRepository.findByOfficerCode("OFF001")).thenReturn(java.util.Optional.of(officer));
        when(reviewRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var response = service.createReview(new CreateReviewRequest("FB100", "KYC", null, "Verify identity", "OFF001"));
        assertEquals("FB100", response.customerNumber());
        assertEquals("KYC", response.reviewType());
    }
}