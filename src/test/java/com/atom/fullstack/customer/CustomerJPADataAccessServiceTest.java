package com.atom.fullstack.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock
    private CustomerRepository customerRepository;
    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        //When
        underTest.selectAllCustomers();
        //Then
        Mockito.verify(customerRepository)
                .findAll();
    }

    @Test
    void selectCustomerById() {
        //Given
        long Id = 1L;
        underTest.selectCustomerById(Id);
        //Then
        Mockito.verify(customerRepository)
                .findById(Id);
    }

    @Test
    void insertCustomer() {
        //Given
        Customer customer = new Customer();
        underTest.insertCustomer(customer);
        //Then
        Mockito.verify(customerRepository)
                .save(customer);

    }

    @Test
    void existsCustomerByEmail() {
        //Given
        String email = "foo@gmail.com";
        // When
        underTest.existsCustomerByEmail(email);
        //Then
        Mockito.verify(customerRepository)
                .existsCustomerByEmail(email);
    }

    @Test
    void existsCustomerById() {
        //Given
        long Id = 1L;
        // When
        underTest.existsCustomerById(Id);
        //Then
        Mockito.verify(customerRepository)
                .existsCustomerById(Id);
    }

    @Test
    void deleteCustomer() {
        // Given
        long Id = 1L;
        // When
        underTest.deleteCustomer(Id);
        //Then
        Mockito.verify(customerRepository)
                .deleteById(Id);
    }

    @Test
    void updateCustomer() {
        //Given
        Customer customer = new Customer();
        underTest.updateCustomer(customer);
        //Then
        Mockito.verify(customerRepository)
                .save(customer);
    }
}