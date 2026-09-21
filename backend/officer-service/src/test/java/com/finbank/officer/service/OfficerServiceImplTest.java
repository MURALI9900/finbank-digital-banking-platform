package com.finbank.officer.service;

import com.finbank.officer.dto.CreateReviewRequest;
import com.finbank.officer.entity.Officer;
import com.finbank.officer.entity.OfficerStatus;
import com.finbank.officer.repository.OfficerRepository;
import com.finbank.officer.repository.OfficerReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfficerServiceImplTest {
    @Mock OfficerRepository officerRepository;
    @Mock OfficerReviewRepository reviewRepository;
    @Mock RestClient.Builder builder;
    @Mock RestClient beneficiaryClient;
    @Mock RestClient kycClient;
    private OfficerServiceImpl service;

    @BeforeEach
    void setUp() {
        when(builder.baseUrl(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(beneficiaryClient, kycClient);
        service = new OfficerServiceImpl(officerRepository, reviewRepository, builder, "http://localhost:8084", "http://localhost:8087");
    }

    @Test
    void shouldCreateReview() {
        Officer officer = new Officer();
        officer.setOfficerCode("OFF001");
        officer.setStatus(OfficerStatus.ACTIVE);
        when(officerRepository.findByOfficerCode("OFF001")).thenReturn(java.util.Optional.of(officer));
        when(reviewRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> invocation.getArgument(0));
        var response = service.createReview(new CreateReviewRequest("FB100", "KYC", null, "Verify identity", "OFF001"));
        assertEquals("FB100", response.customerNumber());
        assertEquals("KYC", response.reviewType());
    }
}
