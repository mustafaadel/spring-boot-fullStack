package com.atom.fullstack.customer;

import com.atom.fullstack.exception.DuplicateResourceException;
import com.atom.fullstack.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CustomerService {


    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc")CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers(){
        log.info("Fetching all customers");
        return customerDao.selectAllCustomers();

    }

    public Customer getCustomerById(Long id){
        log.debug("Fetching customer by id: " + id);
        return customerDao.selectCustomerById(id)
                .map(customer -> {
                    log.info("Customer found: " + customer);
                    return customer;
                })
                .orElseThrow(
                        () ->{
                            log.error("Customer " + id + " does not exist");
                            return new ResourceNotFoundException("Customer " + id + " does not exist");
                        }
                );
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        if (customerDao.existsCustomerByEmail(customerRegistrationRequest.email())){
            throw new DuplicateResourceException("Email already taken");
        }
        log.info("Inserting customer");
        Customer customer = Customer.builder()
                .name(customerRegistrationRequest.name())
                .email(customerRegistrationRequest.email())
                .age(customerRegistrationRequest.age())
                .build();
        customerDao.insertCustomer(customer);
    }

    public void deleteCustomer(Long id){
        log.info("Deleting customer by id: " + id);
        if (!customerDao.existsCustomerById(id)){
            throw new ResourceNotFoundException("Customer " + id + " does not exist");
        }
        customerDao.deleteCustomer(id);
    }

    public void updateCustomer(Long id, CustomerUpdateRequest updateRequest) {
        Customer customer = getCustomerById(id);
        boolean changed = false;

        if (updateRequest.name()  != null && !updateRequest.name().equals(customer.getName())) {
            customer.setName(updateRequest.name());
            changed = true;
        }
        if (updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())) {
            if (customerDao.existsCustomerByEmail(updateRequest.email())) {
                throw new DuplicateResourceException("Email already taken");
            }
            customer.setEmail(updateRequest.email());
            changed = true;
        }
        if (updateRequest.age() != null && !updateRequest.age().equals(customer.getAge())) {
            customer.setAge(updateRequest.age());
            changed = true;
        }

        if (changed) {
            log.info("Updating customer");
            customerDao.updateCustomer(customer);
        } else {
            log.error("No changes detected");
        }
    }
}
