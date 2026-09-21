package com.finbank.customer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.finbank.customer.dto.CreateCustomerRequest;
import com.finbank.customer.dto.CustomerResponse;
import com.finbank.customer.entity.Customer;
import com.finbank.customer.exception.DuplicateCustomerException;
import com.finbank.customer.repository.CustomerRepository;
import com.finbank.customer.service.impl.CustomerServiceImpl;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void shouldCreateCustomer() {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setMobileNumber("+919876543210");
        request.setDateOfBirth(LocalDate.of(1995, 5, 10));
        request.setNationality("Indian");

        when(customerRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(customerRepository.existsByMobileNumber(request.getMobileNumber())).thenReturn(false);
        when(customerRepository.existsByCustomerNumber(any())).thenReturn(false);

        Customer saved = new Customer();
        saved.setCustomerNumber("FB1234567890");
        saved.setFirstName(request.getFirstName());
        saved.setLastName(request.getLastName());
        saved.setEmail(request.getEmail());
        saved.setMobileNumber(request.getMobileNumber());
        saved.setDateOfBirth(request.getDateOfBirth());
        saved.setNationality(request.getNationality());
        saved.setKycVerified(false);

        when(customerRepository.save(any(Customer.class))).thenReturn(saved);

        CustomerResponse response = customerService.createCustomer(request);

        assertEquals("FB1234567890", response.getCustomerNumber());
        assertEquals("John", response.getFirstName());
    }

    @Test
    void shouldRejectDuplicateEmail() {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setEmail("john.doe@example.com");

        when(customerRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(
                DuplicateCustomerException.class,
                () -> customerService.createCustomer(request)
        );
    }
}
