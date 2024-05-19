package com.atom.fullstack.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class CustomerRowMapperTest {


    @Test
    void mapRow() throws SQLException {
        //Given
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet rs = mock(ResultSet.class);

        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("name")).thenReturn("Atom");
        when(rs.getInt("age")).thenReturn(19);
        when(rs.getString("email")).thenReturn("atom@gmail.com");
        Customer actual = new Customer(1L, "Atom", "atom@gmail.com", 19);
        //When
        Customer expected = customerRowMapper.mapRow(rs, 1);
        //Then
        assert expected != null;
        assert actual != null;
        assertThat(actual).isEqualTo(expected);
    }
}