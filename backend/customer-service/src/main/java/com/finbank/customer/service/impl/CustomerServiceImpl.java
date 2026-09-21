package com.finbank.customer.service.impl;

import com.finbank.customer.dto.CreateCustomerRequest;
import com.finbank.customer.dto.CustomerResponse;
import com.finbank.customer.entity.Customer;
import com.finbank.customer.exception.DuplicateCustomerException;
import com.finbank.customer.exception.CustomerNotFoundException;
import com.finbank.customer.repository.CustomerRepository;
import com.finbank.customer.service.CustomerService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateCustomerException("Email is already registered");
        }

        if (customerRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new DuplicateCustomerException("Mobile number is already registered");
        }

        Customer customer = new Customer();
        customer.setCustomerNumber(generateCustomerNumber());
        customer.setFirstName(request.getFirstName().trim());
        customer.setLastName(request.getLastName().trim());
        customer.setEmail(request.getEmail().trim().toLowerCase());
        customer.setMobileNumber(request.getMobileNumber().trim());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setNationality(request.getNationality().trim());
        customer.setKycVerified(false);

        return toResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByNumber(String customerNumber) {
        Customer customer = customerRepository.findByCustomerNumber(customerNumber)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerNumber));

        return toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private String generateCustomerNumber() {
        String number;
        do {
            number = "FB" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        } while (customerRepository.existsByCustomerNumber(number));
        return number;
    }

    private CustomerResponse toResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setCustomerNumber(customer.getCustomerNumber());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setEmail(customer.getEmail());
        response.setMobileNumber(customer.getMobileNumber());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setNationality(customer.getNationality());
        response.setStatus(customer.getStatus());
        response.setKycVerified(customer.isKycVerified());
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedAt(customer.getUpdatedAt());
        return response;
    }
}
