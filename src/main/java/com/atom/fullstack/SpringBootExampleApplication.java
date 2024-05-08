package com.atom.fullstack;

import com.atom.fullstack.customer.Customer;
import com.atom.fullstack.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class SpringBootExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootExampleApplication.class, args);
	}
	@Bean
	CommandLineRunner runner(CustomerRepository customerRepository){
		return args -> {
			Customer James = Customer.builder()
					.name("James")
					.email("james@email.com")
					.age(21)
					.build();
			Customer Maria = Customer.builder()
					.name("Maria")
					.email("maria@email.com")
					.age(23)
					.build();
			List<Customer> customers = List.of(James, Maria);
			//customerRepository.saveAll(customers);
		};
	}
}
