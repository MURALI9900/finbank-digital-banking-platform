package com.finbank.transaction.service;

import com.finbank.transaction.dto.CreateTransactionRequest;
import com.finbank.transaction.dto.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(CreateTransactionRequest request);
    TransactionResponse getTransaction(String transactionReference);
    List<TransactionResponse> getCustomerTransactions(String customerNumber);
}