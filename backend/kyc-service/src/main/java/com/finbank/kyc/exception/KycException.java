package com.finbank.kyc.exception;

public class KycException extends RuntimeException {
    public KycException(String message) {
        super(message);
    }
}