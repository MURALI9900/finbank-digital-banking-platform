package com.finbank.transaction.exception;

public class DuplicateTransactionException extends RuntimeException {
    public DuplicateTransactionException(String message) {
        super(message);
    }
}