package com.atom.fullstack.customer;

import com.atom.fullstack.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestContainers {
    @Autowired
    private CustomerRepository underTest;

    @BeforeEach
    void setUp() {
        
    }

    @Test
    void existsCustomerByEmail() {
        // Given
        Customer customer = Customer.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(faker.number().numberBetween(18, 60))
                .build();
        underTest.save(customer);
        // When
        boolean expected = underTest.existsCustomerByEmail(customer.getEmail());
        // Then
        assertTrue(expected);
    }

    @Test
    void existsCustomerByEmailShouldFail() {
        // Given
        String email = faker.internet().emailAddress() + "-" + UUID.randomUUID();
        // When
        boolean expected = underTest.existsCustomerByEmail(email);
        // Then
        assertFalse(expected);
    }

    @Test
    void existsCustomerById() {
        // Given
        Customer customer = Customer.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(faker.number().numberBetween(18, 60))
                .build();
        underTest.save(customer);
        // When
        boolean expected = underTest.existsCustomerById(customer.getId());
        // Then
        assertTrue(expected);
    }


    @Test
    void existsCustomerByIdShouldFail() {
        // Given
        Long id = -1L;
        // When
        boolean expected = underTest.existsCustomerById(id);
        // Then
        assertFalse(expected);
    }
}