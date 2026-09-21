package com.finbank.account.service;

import com.finbank.account.dto.AccountResponse;
import com.finbank.account.dto.CreateAccountRequest;
import com.finbank.account.entity.Account;
import com.finbank.account.repository.AccountRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setCustomerNumber(request.getCustomerNumber().trim());
        account.setAccountType(request.getAccountType());
        account.setCurrency(request.getCurrency().trim().toUpperCase());
        return toResponse(accountRepository.save(account));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getCustomerAccounts(String customerNumber) {
        return accountRepository.findByCustomerNumber(customerNumber)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private String generateAccountNumber() {
        String number;
        do {
            number = "FB" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }

    private AccountResponse toResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setCustomerNumber(account.getCustomerNumber());
        response.setAccountType(account.getAccountType());
        response.setStatus(account.getStatus());
        response.setCurrency(account.getCurrency());
        response.setBalance(account.getBalance());
        response.setAvailableBalance(account.getAvailableBalance());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());
        return response;
    }
}