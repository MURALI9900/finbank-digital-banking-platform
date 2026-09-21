package com.finbank.customer.service;

import com.finbank.customer.dto.CreateCustomerRequest;
import com.finbank.customer.dto.CustomerResponse;
import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CreateCustomerRequest request);

    CustomerResponse getCustomerByNumber(String customerNumber);

    List<CustomerResponse> getAllCustomers();
}
