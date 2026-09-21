package com.finbank.audit.service;

import com.finbank.audit.dto.AuditResponse;
import com.finbank.audit.dto.CreateAuditRequest;
import com.finbank.audit.entity.*;
import com.finbank.audit.repository.AuditEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {
    @Mock AuditEventRepository repository;
    @InjectMocks AuditServiceImpl service;

    @Test
    void createAuditEvent() {
        CreateAuditRequest request = new CreateAuditRequest();
        request.setActorId("OFF1001");
        request.setActorRole("OFFICER");
        request.setCustomerNumber("FB1001");
        request.setAction(AuditAction.APPROVE);
        request.setResult(AuditResult.SUCCESS);
        request.setEntityType("KYC");
        request.setEntityReference("KYC123");
        request.setDescription("KYC application approved");

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        AuditResponse response = service.create(request);

        assertNotNull(response.getAuditReference());
        assertEquals("FB1001", response.getCustomerNumber());
        assertEquals(AuditResult.SUCCESS, response.getResult());
    }

    @Test
    void getMissingAuditThrowsException() {
        when(repository.findByAuditReference("FAUD404")).thenReturn(java.util.Optional.empty());
        assertThrows(com.finbank.audit.exception.AuditNotFoundException.class, () -> service.get("FAUD404"));
    }
}