package com.atom.fullstack.customer;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class CustomerRowMapper implements RowMapper<Customer>{
    @Override
    public Customer mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Customer customer = new Customer(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getInt("age")
        );
        return customer;
    }

}
