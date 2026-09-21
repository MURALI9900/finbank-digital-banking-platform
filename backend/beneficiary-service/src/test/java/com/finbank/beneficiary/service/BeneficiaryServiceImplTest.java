package com.finbank.beneficiary.service;

import com.finbank.beneficiary.dto.CreateBeneficiaryRequest;
import com.finbank.beneficiary.entity.Beneficiary;
import com.finbank.beneficiary.entity.BeneficiaryStatus;
import com.finbank.beneficiary.exception.DuplicateBeneficiaryException;
import com.finbank.beneficiary.repository.BeneficiaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BeneficiaryServiceImplTest {

    @Mock
    private BeneficiaryRepository repository;

    @InjectMocks
    private BeneficiaryServiceImpl service;

    @Test
    void shouldCreatePendingBeneficiary() {
        when(repository.existsByCustomerNumberAndBeneficiaryAccountNumber("FB100", "FBACC2")).thenReturn(false);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var request = new CreateBeneficiaryRequest("FB100", "Alex", "FBACC2", "FinBank", "FB001", "Alex Account");
        var response = service.createBeneficiary(request);

        assertEquals(BeneficiaryStatus.PENDING, response.status());
        assertEquals("FB100", response.customerNumber());
    }

    @Test
    void shouldRejectDuplicateBeneficiary() {
        when(repository.existsByCustomerNumberAndBeneficiaryAccountNumber("FB100", "FBACC2")).thenReturn(true);

        var request = new CreateBeneficiaryRequest("FB100", "Alex", "FBACC2", "FinBank", null, null);

        assertThrows(DuplicateBeneficiaryException.class, () -> service.createBeneficiary(request));
    }
}