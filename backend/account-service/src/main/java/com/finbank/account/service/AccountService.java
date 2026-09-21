package com.finbank.account.service;

import com.finbank.account.dto.AccountResponse;
import com.finbank.account.dto.CreateAccountRequest;
import java.util.List;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request);

    AccountResponse getAccount(String accountNumber);

    List<AccountResponse> getCustomerAccounts(String customerNumber);
}