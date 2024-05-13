package com.atom.fullstack.customer;

import com.atom.fullstack.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomerJDBCDataAccessServiceTest extends AbstractTestContainers {

    private CustomerJDBCDataAccessService underTest;
    private CustomerRowMapper customerRowMapper = new CustomerRowMapper();
    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        // given
        Customer customer = Customer.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress() + "-" + UUID.randomUUID().toString())
                .age(faker.number().numberBetween(18, 60))
                .build();

        // when
        underTest.insertCustomer(customer);
        List<Customer> customers = underTest.selectAllCustomers();
        // then
        assertTrue(customers.size() > 0);

    }


    @Test
    void selectCustomerById() {
        // Given
        Customer customer = Customer.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress() + "-" + UUID.randomUUID().toString())
                .age(faker.number().numberBetween(18, 60))
                .build();

        // When
        underTest.insertCustomer(customer);

        // Fetch the inserted customer by email
        Optional<Customer> insertedCustomerOpt = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .findFirst();

        // Then
        assertTrue(insertedCustomerOpt.isPresent(), "Inserted customer not found");

        // Get the ID of the inserted customer
        Long insertedCustomerId = insertedCustomerOpt.get().getId();

        // Retrieve the customer by ID from the database
        Optional<Customer> retrievedCustomerOpt = underTest.selectCustomerById(insertedCustomerId);

        // Assert that the customer is retrieved successfully
        assertTrue(retrievedCustomerOpt.isPresent(), "Failed to retrieve customer by ID");

        // Compare the retrieved customer with the originally inserted customer
        assertThat(retrievedCustomerOpt).isPresent().hasValueSatisfying(
                c->{
                    assertThat(c.getId()).isEqualTo(insertedCustomerId);
                    assertThat(c.getName()).isEqualTo(customer.getName());
                    assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.getAge()).isEqualTo(customer.getAge());
                }
        );
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        // Given
        long id = 0;

        // When
        var actual = underTest.selectCustomerById(id);

        // Then
        assertThat(actual).isEmpty();
    }


    @Test
    void insertCustomer() {
        // Given
        Customer customer = Customer.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress() + "-" + UUID.randomUUID().toString())
                .age(faker.number().numberBetween(18, 60))
                .build();

        // When
        underTest.insertCustomer(customer);

        // Then
        List<Customer> customers = underTest.selectAllCustomers();
        assertThat(customers).isNotEmpty();
        assertThat(customers).anyMatch(c -> c.getEmail().equals(customer.getEmail()));
    }

    @Test
    void existsCustomerByEmail() {
        // Given
        Customer customer = Customer.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress() + "-" + UUID.randomUUID().toString())
                .age(faker.number().numberBetween(18, 60))
                .build();

        underTest.insertCustomer(customer);
        // When
        boolean exists = underTest.existsCustomerByEmail(customer.getEmail());

        // Then
        assertTrue(exists);
    }

    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExists() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        boolean actual = underTest.existsCustomerByEmail(email);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerById() {
        // Given
        Customer customer = Customer.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress() + "-" + UUID.randomUUID().toString())
                .age(faker.number().numberBetween(18, 60))
                .build();

        // When
        underTest.insertCustomer(customer);

        // Fetch the inserted customer by email
        Optional<Customer> insertedCustomerOpt = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .findFirst();

        // Then
        assertTrue(insertedCustomerOpt.isPresent(), "Inserted customer not found");

        // Get the ID of the inserted customer
        Long insertedCustomerId = insertedCustomerOpt.get().getId();

        // Assert that the customer exists by ID
        assertTrue(underTest.existsCustomerById(insertedCustomerId));
    }

    @Test
    void existsPersonWithIdWillReturnFalseWhenIdNotPresent() {
        // Given
        long id = -1;

        // When
        var actual = underTest.existsCustomerById(id);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomer() {
        // Given
        Customer customer = Customer.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress() + "-" + UUID.randomUUID().toString())
                .age(faker.number().numberBetween(18, 60))
                .build();
        // When
        underTest.insertCustomer(customer);

        // Fetch the inserted customer by email
        Optional<Customer> insertedCustomerOpt = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .findFirst();

        // Then
        assertTrue(insertedCustomerOpt.isPresent(), "Inserted customer not found");
        underTest.deleteCustomer(insertedCustomerOpt.get().getId());
        assertFalse(underTest.existsCustomerByEmail(customer.getEmail()));
    }

    @Test
    void updateCustomerName() {
        // Given
       Customer customer = Customer.builder()
               .name(faker.name().fullName())
                .email(faker.internet().emailAddress() + "-" + UUID.randomUUID().toString())
                .age(faker.number().numberBetween(18, 60))
                .build();
        underTest.insertCustomer(customer);
        long insertedCustomerId = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .findFirst()
                .get()
                .getId();
        var newName = "boo";
        Customer updatedCustomer = Customer.builder()
                .id(insertedCustomerId)
                .name(newName)
                .email(customer.getEmail())
                .age(customer.getAge())
                .build();
        // When
        underTest.updateCustomer(updatedCustomer);
        // Then
        Optional<Customer> retrievedCustomerOpt = underTest.selectCustomerById(insertedCustomerId);
        assertThat(retrievedCustomerOpt).isPresent().hasValueSatisfying(
                c->{
                    assertThat(c.getId()).isEqualTo(insertedCustomerId);
                    assertThat(c.getName()).isEqualTo(newName);
                    assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.getAge()).isEqualTo(customer.getAge());
                }
        );
    }

    @Test
    void updateCustomerEmail() {
        // Given
        Customer customer = Customer.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress() + "-" + UUID.randomUUID().toString())
                .age(faker.number().numberBetween(18, 60))
                .build();
        underTest.insertCustomer(customer);
        long insertedCustomerId = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .findFirst()
                .get()
                .getId();
        var newEmail = "boo@email.com";
        Customer updatedCustomer = Customer.builder()
                .id(insertedCustomerId)
                .name(customer.getName())
                .email(newEmail)
                .age(customer.getAge())
                .build();
        // When
        underTest.updateCustomer(updatedCustomer);
        // Then
        Optional<Customer> retrievedCustomerOpt = underTest.selectCustomerById(insertedCustomerId);
        assertThat(retrievedCustomerOpt).isPresent().hasValueSatisfying(
                c->{
                    assertThat(c.getId()).isEqualTo(insertedCustomerId);
                    assertThat(c.getName()).isEqualTo(customer.getName());
                    assertThat(c.getEmail()).isEqualTo(newEmail);
                    assertThat(c.getAge()).isEqualTo(customer.getAge());
                }
        );
    }


    @Test
    void updateCustomerAge() {
        // Given
        Customer customer = Customer.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress() + "-" + UUID.randomUUID().toString())
                .age(faker.number().numberBetween(18, 60))
                .build();
        underTest.insertCustomer(customer);
        long insertedCustomerId = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .findFirst()
                .get()
                .getId();
        var newAge = 55;
        Customer updatedCustomer = Customer.builder()
                .id(insertedCustomerId)
                .name(customer.getName())
                .email(customer.getEmail())
                .age(newAge)
                .build();
        // When
        underTest.updateCustomer(updatedCustomer);
        // Then
        Optional<Customer> retrievedCustomerOpt = underTest.selectCustomerById(insertedCustomerId);
        assertThat(retrievedCustomerOpt).isPresent().hasValueSatisfying(
                c->{
                    assertThat(c.getId()).isEqualTo(insertedCustomerId);
                    assertThat(c.getName()).isEqualTo(customer.getName());
                    assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.getAge()).isEqualTo(newAge);
                }
        );
    }

    @Test
    void willUpdateAllPropertiesCustomer(){
        // Given
        Customer customer = Customer.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress() + "-" + UUID.randomUUID().toString())
                .age(faker.number().numberBetween(18, 60))
                .build();
        underTest.insertCustomer(customer);

        long insertedCustomerId = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .findFirst()
                .get()
                .getId();

        //when
        var updatedCustomer = Customer.builder()
                .id(insertedCustomerId)
                .name("new name")
                .email("new email")
                .age(99)
                .build();

        underTest.updateCustomer(updatedCustomer);
        //then
        Optional<Customer> retrievedCustomerOpt = underTest.selectCustomerById(insertedCustomerId);
        assertThat(retrievedCustomerOpt).isPresent().hasValueSatisfying(
                c-> {
                    assertThat(c.getId()).isEqualTo(insertedCustomerId);
                    assertThat(c.getName()).isEqualTo(updatedCustomer.getName());
                    assertThat(c.getEmail()).isEqualTo(updatedCustomer.getEmail());
                    assertThat(c.getAge()).isEqualTo(updatedCustomer.getAge());
                }
        );

    }

    @Test
    void willNotUpdateWhenNothingToUpdate(){
        //Given
        Customer customer = Customer.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress() + "-" + UUID.randomUUID().toString())
                .age(faker.number().numberBetween(18, 60))
                .build();
        underTest.insertCustomer(customer);
         long insertedCustomerId = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .findFirst()
                .get()
                .getId();
        //When
        var updatedCustomer = Customer.builder()
                .id(insertedCustomerId)
                .build();

        underTest.updateCustomer(updatedCustomer);

        //Then
        Optional<Customer> retrievedCustomerOpt = underTest.selectCustomerById(insertedCustomerId);
        assertThat(retrievedCustomerOpt).isPresent().hasValueSatisfying(
                c-> {
                    assertThat(c.getId()).isEqualTo(insertedCustomerId);
                    assertThat(c.getName()).isEqualTo(customer.getName());
                    assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.getAge()).isEqualTo(customer.getAge());
                });
    }

}