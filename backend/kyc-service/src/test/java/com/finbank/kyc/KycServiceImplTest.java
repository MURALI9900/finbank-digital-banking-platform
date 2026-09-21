package com.finbank.kyc;
import com.finbank.kyc.dto.*;
import com.finbank.kyc.entity.*;
import com.finbank.kyc.repository.*;
import com.finbank.kyc.service.KycServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
class KycServiceImplTest{
 @Test void shouldCreateKycApplication(){
  KycApplicationRepository ar=Mockito.mock(KycApplicationRepository.class); KycDocumentRepository dr=Mockito.mock(KycDocumentRepository.class); KycServiceImpl service=new KycServiceImpl(ar,dr);
  KycApplication saved=new KycApplication(); saved.setApplicationReference("KYC123"); saved.setCustomerNumber("FB123"); saved.setStatus(KycStatus.NOT_SUBMITTED); Mockito.when(ar.save(Mockito.any())).thenReturn(saved);
  KycResponse response=service.createApplication(new CreateKycRequest("FB123"));
  assertEquals("KYC123",response.applicationReference()); assertEquals(KycStatus.NOT_SUBMITTED,response.status());
 }
}