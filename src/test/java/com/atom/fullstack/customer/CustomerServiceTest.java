package com.atom.fullstack.customer;

import com.atom.fullstack.exception.DuplicateResourceException;
import com.atom.fullstack.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerDao customerDao;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        //When
        underTest.getAllCustomers();
        //Then
        Mockito.verify(customerDao).selectAllCustomers();
    }

    @Test
    void getCustomerById() {
        //Given
        Long id = 1L;
        Customer customer = new Customer(id, "name", "email", 20);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
        Customer actual = underTest.getCustomerById(id);
        //Then
        assertThat(actual).isEqualTo(customer);
    }


    @Test
    void willThrowWhengetCustomerByIdReturnEmpty() {
        //Given
        Long id = 1L;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());
        //When
//        Customer actual = underTest.getCustomerById(id);
        //Then
        assertThatThrownBy(() -> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer " + id + " does not exist");
    }

    @Test
    void addCustomer() {
        //Given
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest("name", "email", 20);
        // cHECK IF EMAIL EXISTS
        when(customerDao.existsCustomerByEmail(customerRegistrationRequest.email())).thenReturn(false);
        //When
        underTest.addCustomer(customerRegistrationRequest);
        //Then
        //Capture the customer object that is passed to insertCustomer method
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(customerRegistrationRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerRegistrationRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customerRegistrationRequest.age());
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingNewCustomer(){
        // Given
        String email = "email";
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest("name", email, 20);
        when(customerDao.existsCustomerByEmail(email)).thenReturn(true);
        // When
        //underTest.addCustomer(customerRegistrationRequest);
        // Then
        assertThatThrownBy(() -> underTest.addCustomer(customerRegistrationRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken");

        verify(customerDao, Mockito.never()).insertCustomer(Mockito.any());
    }

    @Test
    void deleteCustomer() {
        // Given
        Long id = 1L;
        when(customerDao.existsCustomerById(id)).thenReturn(true);
        // When
        underTest.deleteCustomer(id);
        // Then
        verify(customerDao).deleteCustomer(id);
    }
    @Test
    void willThrowDeleteCustomerByIdWhenIdNotExists(){
        // Given
        Long id = 1L;
        when(customerDao.existsCustomerById(id)).thenReturn(false);
        // When
        //underTest.deleteCustomer(id);
        // Then
        assertThatThrownBy(() -> underTest.deleteCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer " + id + " does not exist");
        verify(customerDao, Mockito.never()).deleteCustomer(id);
    }

    @Test
    void updateCustomer() {
        Long id = 1L;
        Customer customer = new Customer(id, "name", "email", 20);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("new name", "new email", 30);
        when(customerDao.existsCustomerByEmail(updateRequest.email())).thenReturn(false);
        underTest.updateCustomer(id, updateRequest);
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
    }
}