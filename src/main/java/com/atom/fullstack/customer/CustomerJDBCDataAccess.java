package com.atom.fullstack.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository("jdbc")
@RequiredArgsConstructor
public class CustomerJDBCDataAccess implements CustomerDao{
    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;
    @Override
    public List<Customer> selectAllCustomers() {
        var sql = """
                SELECT id, name, email, age
                FROM customer
                """;

       return jdbcTemplate.query(sql, customerRowMapper);

    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        var sql = """
                SELECT id, name, email, age
                FROM customer
                WHERE id = ?
                """;
        try {
            return  Optional.ofNullable(jdbcTemplate.queryForObject(sql, customerRowMapper, id));
        } catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
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
        String sql = """
                SELECT EXISTS(
                SELECT 1
                FROM customer
                WHERE email = ?
                )
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, email));
    }

    @Override
    public boolean existsCustomerById(Long id) {
        String sql = """
                SELECT EXISTS(
                SELECT 1
                FROM customer
                WHERE id = ?
                )
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));

    }

    @Override
    public void deleteCustomer(Long id) {
        var sql = """
                DELETE FROM customer
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, id);

    }

    @Override
    public void updateCustomer(Customer customer) {
        var sql = """
                update customer
                set name = ?,
                email = ?,
                age = ?
                where id = ?
                """;
        jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge(), customer.getId());
    }
}
