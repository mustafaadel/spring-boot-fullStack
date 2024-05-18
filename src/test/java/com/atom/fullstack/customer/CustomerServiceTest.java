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
        //Given
        Long Id = 1L;
        when(customerDao.existsCustomerById(Id)).thenReturn(true);
        //When
        underTest.deleteCustomer(Id);
        //Then
        verify(customerDao).deleteCustomer(Id);
    }

    @Test
    void willThrowWhenDeleteCustomerByIdNotExists(){
        //Given
        Long id = -1L;
        when(customerDao.existsCustomerById(id)).thenReturn(false);
        //When
        assertThatThrownBy(()->
                underTest.deleteCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer " + id + " does not exist");
        //Then
        verify(customerDao, Mockito.never()).deleteCustomer(Mockito.any());
    }

    @Test
    void canUpdateAllCustomerProperties() {
        //Given
        Long id = 1L;
        Customer customer = new Customer(id,"name", "email@email",21);
        //When
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest("new name", "new email", 23);
        when(customerDao.existsCustomerByEmail("new email")).thenReturn(false);
        underTest.updateCustomer(id, customerUpdateRequest);
        //Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getAge()).isEqualTo(customerUpdateRequest.age());
        assertThat(capturedCustomer.getName()).isEqualTo(customerUpdateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerUpdateRequest.email());

    }


    @Test
    void canUpdateCustomerName() {
        //Given
        Long id = 1L;
        Customer customer = new Customer(id,"name", "email@email",21);
        //When
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest("new name", null, null);
//        when(customerDao.existsCustomerByEmail("new email")).thenReturn(false);
        underTest.updateCustomer(id, customerUpdateRequest);
        //Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getName()).isEqualTo(customerUpdateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());

    }

    @Test
    void canUpdateCustomerAge() {
        //Given
        Long id = 1L;
        Customer customer = new Customer(id,"name", "email@email",21);
        //When
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(null, null, 23);
//        when(customerDao.existsCustomerByEmail("new email")).thenReturn(false);
        underTest.updateCustomer(id, customerUpdateRequest);
        //Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getAge()).isEqualTo(customerUpdateRequest.age());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());

    }

    @Test
    void canUpdateCustomerEmail() {
        //Given
        Long id = 1L;
        Customer customer = new Customer(id,"name", "email@email",21);
        //When
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(null, "new email", null);
        when(customerDao.existsCustomerByEmail("new email")).thenReturn(false);
        underTest.updateCustomer(id, customerUpdateRequest);
        //Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void willThrowWhenEmailTakenWhileUpdate(){
        //Given
        Long id = 1L;
        Customer customer = new Customer(id,"name", "email@email",21);
        //When
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "new email";
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(null, newEmail, null);
        when(customerDao.existsCustomerByEmail(newEmail)).thenReturn(true);

        //underTest.updateCustomer(id, customerUpdateRequest);\
        assertThatThrownBy(()-> underTest.updateCustomer(id,customerUpdateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken");
        //Then
        verify(customerDao , Mockito.never()).updateCustomer(Mockito.any());

    }
}