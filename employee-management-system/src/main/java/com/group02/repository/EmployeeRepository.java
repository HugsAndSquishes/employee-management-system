package com.group02.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.SQLException;
import com.group02.model.Employee;
import com.group02.util.DatabaseUtil;
//import com.group02.model.Employee;

public class EmployeeRepository {

    public int save(Employee employee) {
        String sql = "INSERT INTO employees (name, department) VALUES (?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, employee.getName());
            // stmt.setString(2, employee.getEmail());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating employee failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int empID = generatedKeys.getInt(1);
                    employee.setEmpID(empID);
                    return empID;
                } else {
                    throw new SQLException("Creating employee failed, no ID obtained.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Optional<Employee> findById(int empID) {
        String sql = "SELECT * FROM employees WHERE empID = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = new Employee();
                    employee.setEmpID(rs.getInt("empID"));
                    employee.setName(rs.getString("name"));
                    // employee.setEmail(rs.getString("email"));
                    return Optional.of(employee);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";

        try (Connection conn = DatabaseUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Employee employee = new Employee();
                employee.setEmpID(rs.getInt("empID"));
                employee.setName(rs.getString("name"));
                // employee.setEmail(rs.getString("email"));
                employees.add(employee);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    // Additional methods like update, delete, etc.
}