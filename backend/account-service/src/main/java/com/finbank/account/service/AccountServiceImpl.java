package com.finbank.account.service;

import com.finbank.account.dto.*;
import com.finbank.account.entity.*;
import com.finbank.account.exception.AccountOperationException;
import com.finbank.account.repository.AccountRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    public AccountServiceImpl(AccountRepository accountRepository){this.accountRepository=accountRepository;}

    @Override
    public AccountResponse createAccount(CreateAccountRequest request){
        Account account=new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setCustomerNumber(request.getCustomerNumber().trim().toUpperCase());
        account.setAccountType(request.getAccountType());
        account.setCurrency(request.getCurrency().trim().toUpperCase());
        return toResponse(accountRepository.save(account));
    }

    @Override @Transactional(readOnly=true)
    public AccountResponse getAccount(String accountNumber){
        return accountRepository.findByAccountNumber(accountNumber.trim().toUpperCase()).map(this::toResponse)
                .orElseThrow(()->new AccountOperationException("Account not found: "+accountNumber));
    }

    @Override @Transactional(readOnly=true)
    public List<AccountResponse> getCustomerAccounts(String customerNumber){
        return accountRepository.findByCustomerNumber(customerNumber.trim().toUpperCase()).stream().map(this::toResponse).toList();
    }

    @Override
    public void applyBalanceTransaction(BalanceTransactionRequest request){
        String type=request.getType().trim().toUpperCase();
        if(!type.equals("DEPOSIT")&&!type.equals("WITHDRAWAL")&&!type.equals("TRANSFER"))
            throw new AccountOperationException("Unsupported balance transaction type");
        if(type.equals("DEPOSIT")){creditOwned(request.getDestinationAccountNumber(),request.getCustomerNumber(),request.getAmount(),request.getCurrency());return;}
        if(type.equals("WITHDRAWAL")){debitOwned(request.getSourceAccountNumber(),request.getCustomerNumber(),request.getAmount(),request.getCurrency());return;}
        String source=request.getSourceAccountNumber().trim().toUpperCase();
        String destination=request.getDestinationAccountNumber().trim().toUpperCase();
        if(source.equals(destination)) throw new AccountOperationException("Source and destination accounts must be different");
        debitOwned(source,request.getCustomerNumber(),request.getAmount(),request.getCurrency());
        credit(destination,request.getAmount(),request.getCurrency());
    }

    private void creditOwned(String number,String customerNumber,BigDecimal amount,String currency){
        Account account=findActiveForUpdate(number);
        validateOwnership(account,customerNumber);
        validateCurrency(account,currency);
        account.setBalance(account.getBalance().add(amount));
        account.setAvailableBalance(account.getAvailableBalance().add(amount));
    }

    private void debitOwned(String number,String customerNumber,BigDecimal amount,String currency){
        Account account=findActiveForUpdate(number);
        validateOwnership(account,customerNumber);
        validateCurrency(account,currency);
        if(account.getAvailableBalance().compareTo(amount)<0) throw new AccountOperationException("Insufficient available balance");
        account.setBalance(account.getBalance().subtract(amount));
        account.setAvailableBalance(account.getAvailableBalance().subtract(amount));
    }

    private void credit(String number,BigDecimal amount,String currency){
        Account account=findActiveForUpdate(number);
        validateCurrency(account,currency);
        account.setBalance(account.getBalance().add(amount));
        account.setAvailableBalance(account.getAvailableBalance().add(amount));
    }

    private void debit(String number,BigDecimal amount,String currency){
        Account account=findActiveForUpdate(number);
        validateCurrency(account,currency);
        if(account.getAvailableBalance().compareTo(amount)<0) throw new AccountOperationException("Insufficient available balance");
        account.setBalance(account.getBalance().subtract(amount));
        account.setAvailableBalance(account.getAvailableBalance().subtract(amount));
    }

    private Account findActiveForUpdate(String number){
        if(number==null||number.isBlank()) throw new AccountOperationException("Account number is required");
        Account account=accountRepository.findByAccountNumberForUpdate(number.trim().toUpperCase())
                .orElseThrow(()->new AccountOperationException("Account not found: "+number));
        if(account.getStatus()!=AccountStatus.ACTIVE) throw new AccountOperationException("Account is not active: "+number);
        return account;
    }

    private void validateOwnership(Account account,String customerNumber){
        if(customerNumber==null||!account.getCustomerNumber().equalsIgnoreCase(customerNumber.trim())) throw new AccountOperationException("Account does not belong to customer");
    }

    private void validateCurrency(Account account,String currency){
        if(currency==null||!account.getCurrency().equalsIgnoreCase(currency.trim()))
            throw new AccountOperationException("Account currency does not match transaction currency");
    }

    private String generateAccountNumber(){
        String number;
        do{number="FB"+UUID.randomUUID().toString().replace("-","").substring(0,12).toUpperCase();}
        while(accountRepository.existsByAccountNumber(number));
        return number;
    }

    private AccountResponse toResponse(Account account){
        AccountResponse response=new AccountResponse();
        response.setId(account.getId()); response.setAccountNumber(account.getAccountNumber());
        response.setCustomerNumber(account.getCustomerNumber()); response.setAccountType(account.getAccountType());
        response.setStatus(account.getStatus()); response.setCurrency(account.getCurrency());
        response.setBalance(account.getBalance()); response.setAvailableBalance(account.getAvailableBalance());
        response.setCreatedAt(account.getCreatedAt()); response.setUpdatedAt(account.getUpdatedAt());
        return response;
    }
}