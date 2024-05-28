package com.atom.fullstack;

import com.atom.fullstack.customer.Customer;
import com.atom.fullstack.customer.CustomerRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootExampleApplication.class, args);
	}
	@Bean
	CommandLineRunner runner(CustomerRepository customerRepository){
		return args -> {
			Faker faker = new Faker();
			Customer customer = Customer.builder()
					.name(faker.name().fullName())
					.email(faker.internet().emailAddress())
					.age(faker.number().numberBetween(18, 60))
					.build();

			customerRepository.save(customer);
		};
	}
}
