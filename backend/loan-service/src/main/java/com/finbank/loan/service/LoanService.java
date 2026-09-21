package com.finbank.loan.service;

import com.finbank.loan.dto.*;
import java.util.List;

public interface LoanService {
    LoanResponse apply(CreateLoanRequest r);
    LoanResponse get(String ref);
    List<LoanResponse> customerLoans(String customer);
    LoanResponse decide(String ref, LoanDecisionRequest r);
    LoanResponse disburse(String ref, DisbursementRequest r);
    RepaymentResponse createRepayment(RepaymentRequest r);
    RepaymentResponse pay(String ref, PaymentRequest r);
    List<RepaymentResponse> repayments(String loanRef);
}
