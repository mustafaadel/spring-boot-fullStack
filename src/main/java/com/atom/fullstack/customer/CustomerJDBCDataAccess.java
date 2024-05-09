package com.atom.fullstack.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository("jdbc")
@RequiredArgsConstructor
public class CustomerJDBCDataAccess implements CustomerDao{
    private final JdbcTemplate jdbcTemplate;
    @Override
    public List<Customer> selectAllCustomers() {
        return null;
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        return Optional.empty();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer (name, email, age)
                VALUES (?, ?, ?)
                """;
            int res = jdbcTemplate.update(sql,
                    customer.getName(),
                    customer.getEmail(),
                    customer.getAge()
                    );
        System.out.println("jdbc insert res: " + res);
    }

    @Override
    public boolean existsCustomerByEmail(String email) {
        return false;
    }

    @Override
    public boolean existsCustomerById(Long id) {
        return false;
    }

    @Override
    public void deleteCustomer(Long id) {

    }

    @Override
    public void updateCustomer(Customer customer) {

    }
}
