package com.lhind.internship.jdbc.repository;

import com.lhind.internship.jdbc.mapper.EmployeeMapper;
import com.lhind.internship.jdbc.model.Employee;
import com.lhind.internship.jdbc.model.enums.EmployeeQuery;
import com.lhind.internship.jdbc.util.JdbcConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeRepository implements Repository<Employee, Integer> {

    private static final String SELECT_ALL = "SELECT * FROM employees;";
    private static final String SELECT_BY_ID = "SELECT * FROM employees WHERE employeeNumber = ?;";
    private static final String CHECK_EXISTANCE = "SELECT 1 FROM employees WHERE employeeNumber = ?;";
    private static final String UPDATE_EMPLOYEE = "UPDATE employees SET name = ?, email = ? WHERE employeeNumber = ?;";
    private static final String INSERT_EMPLOYEE = "INSERT INTO employees (employeeNumber, name, email) VALUES (?, ?, ?);";
    private static final String DELETE_EMPLOYEE = "DELETE FROM employees WHERE employeeNumber = ?;";
    private EmployeeMapper employeeMapper = EmployeeMapper.getInstance();

    @Override
    public List<Employee> findAll() {
        final List<Employee> response = new ArrayList<>();
        try (final Connection connection = JdbcConnection.connect();
             final PreparedStatement statement = connection.prepareStatement(EmployeeQuery.SELECT_ALL.getQuery())) {
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                response.add(employeeMapper.toEntity(result));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return response;
    }

    @Override
    public Optional<Employee> findById(final Integer id) {
        try (final Connection connection = JdbcConnection.connect();
             final PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setInt(1, id);

            final ResultSet result = statement.executeQuery();

            if (result.next()) {
                final Employee employee = employeeMapper.toEntity(result);
                return Optional.of(employee);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean exists(final Integer integer) {
        // TODO: Implement a method which checks if an employee with the given id exists in the employees table
        try(final Connection connection = JdbcConnection.connect();
            final PreparedStatement statement = connection.prepareStatement(CHECK_EXISTANCE)){
            statement.setInt(1 , integer);
            final ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
        return false;
    }

    @Override
    public Employee save(final Employee employee) {
        /*
         * TODO: Implement a method which adds an employee to the employees table
         *  If the employee exists then the method should instead update the employee
         *
         */

        if(employee == null){
            try {
                throw new IllegalAccessException("Employee cannot be null");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        try(final Connection connection = JdbcConnection.connect()){
            if(exists(employee.getEmployeeNumber())){
                try(final PreparedStatement statement = connection.prepareStatement(UPDATE_EMPLOYEE)){
                    statement.setInt(1, employee.getEmployeeNumber());
                    statement.setString(2, employee.getLastName());
                    statement.setString(3, employee.getFirstName());
                    statement.setString(4, employee.getExtension());
                    statement.executeUpdate();
                }
            }else {
                //create new  record
                try (final PreparedStatement statement = connection.prepareStatement(INSERT_EMPLOYEE)) {
                    statement.setInt(1, employee.getEmployeeNumber());
                    statement.setString(2, employee.getLastName());
                    statement.setString(3, employee.getFirstName());
                    statement.setString(4, employee.getExtension());
                    statement.setString(5, employee.getEmail());
                    statement.setString(6, employee.getOfficeCode());
                    statement.setInt(7, employee.getReportsTo());
                    statement.setString(8, employee.getJobTitle());
                    statement.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return employee;
    }

    @Override
    public void delete(final Integer integer) {
        /*
         * TODO: Implement a method which deletes an employee given the id
         */
        try(final Connection connection = JdbcConnection.connect();
            final PreparedStatement statement = connection.prepareStatement(DELETE_EMPLOYEE)){
            statement.setInt(1 , integer);
            statement.executeUpdate();
        }catch (Exception e){
            System.err.println(e.getMessage());
        }

    }
}
