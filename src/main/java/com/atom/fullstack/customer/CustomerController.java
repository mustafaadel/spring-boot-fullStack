package com.atom.fullstack.customer;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public List<Customer> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    @GetMapping( "{customerId}")
    public Customer getCustomerById(@PathVariable("customerId") Long id){
        return customerService.getCustomerById(id);
    }

    @PostMapping
    public void addCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest) {
        customerService.addCustomer(customerRegistrationRequest);
    }

    @DeleteMapping("{customerId}")
    public void deleteCustomer(@PathVariable("customerId") Long id){
        customerService.deleteCustomer(id);
    }

    @PatchMapping("{customerId}")
    public void updateCustomer(@PathVariable("customerId") Long id, @RequestBody CustomerUpdateRequest customerUpdateRequest){
        customerService.updateCustomer(id, customerUpdateRequest);
    }
}
