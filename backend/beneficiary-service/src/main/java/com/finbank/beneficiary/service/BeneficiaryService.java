package com.finbank.beneficiary.service;

import com.finbank.beneficiary.dto.*;
import java.util.List;

public interface BeneficiaryService {
    BeneficiaryResponse createBeneficiary(CreateBeneficiaryRequest request);
    BeneficiaryResponse getBeneficiary(String reference);
    List<BeneficiaryResponse> getCustomerBeneficiaries(String customerNumber);
    BeneficiaryResponse decideBeneficiary(String reference, BeneficiaryDecisionRequest request);
}