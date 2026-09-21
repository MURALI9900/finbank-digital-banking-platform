package com.finbank.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BalanceTransactionRequest {
    @NotBlank private String transactionReference;
    @NotBlank private String customerNumber;
    @NotBlank private String type;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    @NotNull @DecimalMin("0.01") private BigDecimal amount;
    @NotBlank private String currency;
    public String getTransactionReference(){return transactionReference;}
    public String getCustomerNumber(){return customerNumber;}
    public void setCustomerNumber(String v){customerNumber=v;}
    public void setTransactionReference(String v){transactionReference=v;}
    public String getType(){return type;}
    public void setType(String v){type=v;}
    public String getSourceAccountNumber(){return sourceAccountNumber;}
    public void setSourceAccountNumber(String v){sourceAccountNumber=v;}
    public String getDestinationAccountNumber(){return destinationAccountNumber;}
    public void setDestinationAccountNumber(String v){destinationAccountNumber=v;}
    public BigDecimal getAmount(){return amount;}
    public void setAmount(BigDecimal v){amount=v;}
    public String getCurrency(){return currency;}
    public void setCurrency(String v){currency=v;}
}