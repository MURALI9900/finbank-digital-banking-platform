package com.finbank.beneficiary.exception;

public class BeneficiaryNotFoundException extends RuntimeException {
    public BeneficiaryNotFoundException(String message) {
        super(message);
    }
}