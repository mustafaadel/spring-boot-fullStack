package com.atom.fullstack.journey;

import com.atom.fullstack.customer.Customer;
import com.atom.fullstack.customer.CustomerRegistrationRequest;
import com.atom.fullstack.customer.CustomerUpdateRequest;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void canRegisterCustomer() {
        //Create Registeration Request
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress();
        int age = faker.number().numberBetween(18, 60);
        CustomerRegistrationRequest customerRegistrationRequest =
                new CustomerRegistrationRequest(name, email, age);
        //Send Request
        webTestClient.post()
                .uri("/api/v1/customer")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .bodyValue(customerRegistrationRequest)
                .exchange()// Send Request
                .expectStatus().isOk();
        //Get All Customers
        List<Customer> customerList = webTestClient.get()
                .uri("api/v1/customer")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        //Assert Customer is in the list
        Customer expected = Customer.builder()
                .name(name)
                .email(email)
                .age(age)
                .build();

        assertThat(customerList).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expected);

        // Get Customer by Id
        Long id = customerList.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        expected.setId(id);

        webTestClient.get()
                .uri("api/v1/customer" + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expected);

        // Delete Customer
        webTestClient.delete()
                .uri("api/v1/customer" + "/{id}", id)
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void canDeleteCustomer() {
        //Create Registeration Request
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress();
        int age = faker.number().numberBetween(18, 60);
        CustomerRegistrationRequest customerRegistrationRequest =
                new CustomerRegistrationRequest(name, email, age);
        //Send Request
        webTestClient.post()
                .uri("/api/v1/customer")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .bodyValue(customerRegistrationRequest)
                .exchange()// Send Request
                .expectStatus().isOk();
        //Get All Customers
        List<Customer> customerList = webTestClient.get()
                .uri("api/v1/customer")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();


        // Get Customer by Id
        Long id = customerList.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        webTestClient.delete()
                .uri("api/v1/customer" + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.get()
                .uri("api/v1/customer" + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        //Create Registeration Request
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress();
        int age = faker.number().numberBetween(18, 60);
        CustomerRegistrationRequest customerRegistrationRequest =
                new CustomerRegistrationRequest(name, email, age);
        //Send Request
        webTestClient.post()
                .uri("/api/v1/customer")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .bodyValue(customerRegistrationRequest)
                .exchange()// Send Request
                .expectStatus().isOk();
        //Get All Customers
        List<Customer> customerList = webTestClient.get()
                .uri("api/v1/customer")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();


        // Get Customer by Id
        Long id = customerList.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                "new name",
                null,
                null
        );

        webTestClient.patch()
                .uri("api/v1/customer" + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerUpdateRequest)
                .exchange()
                .expectStatus()
                .isOk();

        Customer updatedCustomer = webTestClient.get()
                .uri("api/v1/customer" + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expectedCustomer = Customer.builder()
                .id(id)
                .name("new name")
                .email(email)
                .age(age)
                .build();

        assertThat(updatedCustomer).isEqualTo(expectedCustomer);
    }
}
