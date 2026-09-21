package com.finbank.loan.service;

import com.finbank.loan.dto.*;
import com.finbank.loan.entity.*;
import com.finbank.loan.exception.LoanException;
import com.finbank.loan.repository.LoanApplicationRepository;
import com.finbank.loan.repository.LoanRepaymentRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class LoanServiceImpl implements LoanService {
    private final LoanApplicationRepository loanRepository;
    private final LoanRepaymentRepository repaymentRepository;
    private final RestClient transactionClient;

    public LoanServiceImpl(LoanApplicationRepository loanRepository, LoanRepaymentRepository repaymentRepository) {
        this(loanRepository, repaymentRepository, RestClient.builder(), "http://localhost:8083");
    }

    public LoanServiceImpl(LoanApplicationRepository loanRepository, LoanRepaymentRepository repaymentRepository,
                           RestClient.Builder restClientBuilder, @org.springframework.beans.factory.annotation.Value("${finbank.services.transaction-url:http://localhost:8083}") String transactionServiceUrl) {
        this.loanRepository = loanRepository;
        this.repaymentRepository = repaymentRepository;
        this.transactionClient = restClientBuilder.baseUrl(transactionServiceUrl).build();
    }

    @Override
    public LoanResponse apply(CreateLoanRequest r) {
        LoanApplication loan = new LoanApplication();
        loan.setApplicationReference(reference("LN"));
        loan.setCustomerNumber(r.customerNumber().trim().toUpperCase());
        loan.setLoanType(r.loanType());
        loan.setStatus(LoanStatus.SUBMITTED);
        loan.setRequestedAmount(r.requestedAmount().setScale(2));
        loan.setTenureMonths(r.tenureMonths());
        loan.setAnnualInterestRate(r.annualInterestRate());
        loan.setPurpose(r.purpose());
        loan.setSubmittedAt(java.time.LocalDateTime.now());
        return toLoanResponse(loanRepository.save(loan));
    }

    @Override
    public LoanResponse get(String ref) {
        return toLoanResponse(findLoan(ref));
    }

    @Override
    public List<LoanResponse> customerLoans(String customer) {
        return loanRepository.findByCustomerNumberOrderByCreatedAtDesc(customer.trim().toUpperCase()).stream()
                .map(this::toLoanResponse).toList();
    }

    @Override
    public LoanResponse decide(String ref, LoanDecisionRequest r) {
        LoanApplication loan = findLoan(ref);
        if (loan.getStatus() != LoanStatus.SUBMITTED && loan.getStatus() != LoanStatus.UNDER_REVIEW)
            throw new LoanException("Loan is not available for decision");
        if (r.status() != LoanStatus.APPROVED && r.status() != LoanStatus.REJECTED)
            throw new LoanException("Decision must be APPROVED or REJECTED");
        if (r.status() == LoanStatus.REJECTED && (r.rejectionReason() == null || r.rejectionReason().isBlank()))
            throw new LoanException("Rejection reason is required");
        loan.setStatus(r.status());
        loan.setReviewedBy(r.reviewedBy());
        loan.setReviewedAt(java.time.LocalDateTime.now());
        loan.setRejectionReason(r.rejectionReason());
        if (r.approvedInterestRate() != null) loan.setAnnualInterestRate(r.approvedInterestRate());
        return toLoanResponse(loanRepository.save(loan));
    }

    @Override
    public LoanResponse disburse(String ref, DisbursementRequest r) {
        LoanApplication loan = findLoan(ref);
        if (loan.getStatus() != LoanStatus.APPROVED) throw new LoanException("Only approved loans can be disbursed");
        TransactionResponse transaction = createTransaction(new TransactionRequest(
                loan.getCustomerNumber(), null, r.destinationAccountNumber().trim(), "DEPOSIT",
                loan.getRequestedAmount(), r.currency().trim().toUpperCase(),
                "Loan disbursement " + loan.getApplicationReference(), "LOAN-DISBURSE-" + loan.getApplicationReference()));
        if (!"SUCCESS".equals(transaction.status().name())) throw new LoanException("Loan disbursement transaction failed");
        loan.setStatus(LoanStatus.DISBURSED);
        loan.setDisbursedAt(java.time.LocalDateTime.now());
        return toLoanResponse(loanRepository.save(loan));
    }

    @Override
    public RepaymentResponse createRepayment(RepaymentRequest r) {
        LoanApplication loan = findLoan(r.loanReference());
        if (loan.getStatus() != LoanStatus.DISBURSED) throw new LoanException("Repayment is allowed only for disbursed loans");
        if (repaymentRepository.findByLoanReferenceOrderByInstallmentNumberAsc(r.loanReference()).stream()
                .anyMatch(x -> x.getInstallmentNumber().equals(r.installmentNumber())))
            throw new LoanException("Installment already exists");
        LoanRepayment repayment = new LoanRepayment();
        repayment.setRepaymentReference(reference("RP"));
        repayment.setLoanReference(r.loanReference());
        repayment.setInstallmentNumber(r.installmentNumber());
        repayment.setDueDate(r.dueDate());
        repayment.setDueAmount(r.dueAmount().setScale(2));
        repayment.setStatus(RepaymentStatus.PENDING);
        return toRepaymentResponse(repaymentRepository.save(repayment));
    }

    @Override
    public RepaymentResponse pay(String ref, PaymentRequest r) {
        LoanRepayment repayment = repaymentRepository.findByRepaymentReference(ref)
                .orElseThrow(() -> new LoanException("Repayment not found"));
        LoanApplication loan = findLoan(repayment.getLoanReference());
        if (loan.getStatus() != LoanStatus.DISBURSED) throw new LoanException("Loan is not open for repayment");
        if (repayment.getStatus() == RepaymentStatus.PAID) throw new LoanException("Repayment is already paid");
        if (r.sourceAccountNumber() == null || r.sourceAccountNumber().isBlank())
            throw new LoanException("Source account number is required");
        if (r.amount().compareTo(repayment.getDueAmount()) > 0) throw new LoanException("Payment cannot exceed due amount");
        TransactionResponse transaction = createTransaction(new TransactionRequest(
                loan.getCustomerNumber(), r.sourceAccountNumber().trim(), null, "WITHDRAWAL",
                r.amount(), r.currency().trim().toUpperCase(),
                "Loan repayment " + repayment.getRepaymentReference(), "LOAN-REPAY-" + repayment.getRepaymentReference()));
        if (!"SUCCESS".equals(transaction.status().name())) throw new LoanException("Loan repayment transaction failed");
        repayment.setPaidAmount(r.amount().setScale(2));
        repayment.setPaidDate(LocalDate.now());
        repayment.setStatus(r.amount().compareTo(repayment.getDueAmount()) == 0 ? RepaymentStatus.PAID : RepaymentStatus.PARTIAL);
        return toRepaymentResponse(repaymentRepository.save(repayment));
    }

    @Override
    public List<RepaymentResponse> repayments(String loanRef) {
        return repaymentRepository.findByLoanReferenceOrderByInstallmentNumberAsc(loanRef).stream()
                .map(this::toRepaymentResponse).toList();
    }

    private TransactionResponse createTransaction(TransactionRequest request) {
        return transactionClient.post().uri("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON).body(request).retrieve()
                .body(TransactionResponse.class);
    }

    private LoanApplication findLoan(String ref) {
        return loanRepository.findByApplicationReference(ref)
                .orElseThrow(() -> new LoanException("Loan application not found"));
    }

    private String reference(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    private LoanResponse toLoanResponse(LoanApplication x) {
        return new LoanResponse(x.getApplicationReference(), x.getCustomerNumber(), x.getLoanType(), x.getStatus(),
                x.getRequestedAmount(), x.getTenureMonths(), x.getAnnualInterestRate(), x.getPurpose(),
                x.getReviewedBy(), x.getRejectionReason(), x.getSubmittedAt(), x.getReviewedAt(), x.getDisbursedAt());
    }

    private RepaymentResponse toRepaymentResponse(LoanRepayment x) {
        return new RepaymentResponse(x.getRepaymentReference(), x.getLoanReference(), x.getInstallmentNumber(),
                x.getDueDate(), x.getDueAmount(), x.getPaidAmount(), x.getStatus(), x.getPaidDate());
    }

    private record TransactionResponse(String transactionReference, String idempotencyKey, String customerNumber,
                                       String sourceAccountNumber, String destinationAccountNumber,
                                       String type, String status,
                                       BigDecimal amount, String currency, String description,
                                       java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt) {}
}
