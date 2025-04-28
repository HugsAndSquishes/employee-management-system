// src/main/java/com/group02/repository/EmployeeManager.java
package com.group02.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.group02.model.Employee;
import com.group02.util.DatabaseUtil;

/**
 * Manages CRUD operations on 'employees' table.
 * <p>
 * Implements Searchable for read queries and Updatable for single-column updates.
 * Uses DatabaseUtil to obtain JDBC connections.
 * </p>
 */
public class EmployeeManager implements Searchable, Updatable {

    /**
     * Inserts a new Employee into the DB.
     * Uses RETURN_GENERATED_KEYS to fetch the auto-increment empID.
     *
     * @param employee DTO with fields (name, division, SSN, jobTitle, salary, payInfo)
     * @return generated empID (>0) or -1 on failure
     */
    public int addEmployee(Employee employee) {
        String sql = "INSERT INTO employees (employeeName, division, SSN, jobTitle, salary, payInfo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // bind parameters in the same order as the INSERT columns
            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getDivision());
            stmt.setString(3, employee.getSSN());
            stmt.setString(4, employee.getJobTitle());
            stmt.setDouble(5, employee.getSalary());
            stmt.setString(6, employee.getPayInfo());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                // no insert happened
                throw new SQLException("Creating employee failed, no rows affected.");
            }

            // retrieve the generated key
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int empID = keys.getInt(1);
                    employee.setEmpID(empID);
                    return empID;
                } else {
                    throw new SQLException("Creating employee failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  // consider replacing with a logger
            return -1;
        }
    }

    /**
     * Maps current ResultSet row to an Employee object.
     * @param rs positioned at a valid row
     * @return populated Employee instance
     * @throws SQLException on column access errors
     */
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setEmpID(rs.getInt("empID"));
        e.setName(rs.getString("employeeName"));
        e.setSSN(rs.getString("SSN"));
        e.setJobTitle(rs.getString("jobTitle"));
        e.setDivision(rs.getString("division"));
        e.setSalary(rs.getDouble("salary"));
        e.setPayInfo(rs.getString("payInfo"));
        return e;
    }

    /** {@inheritDoc} */
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    /** {@inheritDoc} */
    public Optional<Employee> searchByID(int empID) {
        String sql = "SELECT * FROM employees WHERE empID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /** {@inheritDoc} */
    public Optional<Employee> searchBySSN(String SSN) {
        String sql = "SELECT * FROM employees WHERE SSN = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, SSN);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /** {@inheritDoc} */
    public List<Employee> searchByName(String namePattern) {
        String sql = "SELECT * FROM employees WHERE employeeName REGEXP ?";
        List<Employee> employees = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, namePattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSetToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    /**
     * Updates all fields on an existing employee record.
     * @param employee DTO with empID and new field values
     * @return true if update affected ≥1 row
     */
    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE employees SET employeeName=?, division=?, SSN=?, jobTitle=?, salary=?, payInfo=? WHERE empID=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getDivision());
            stmt.setString(3, employee.getSSN());
            stmt.setString(4, employee.getJobTitle());
            stmt.setDouble(5, employee.getSalary());
            stmt.setString(6, employee.getPayInfo());
            stmt.setInt(7, employee.getEmpID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** {@inheritDoc} */
    public boolean updateField(int empID, String fieldName, Object fieldValue) {
        // Validate allowed columns
        List<String> validFields = List.of("employeeName","division","SSN","jobTitle","salary","payInfo");
        if (!validFields.contains(fieldName)) {
            throw new IllegalArgumentException("Invalid field: " + fieldName);
        }
        String sql = "UPDATE employees SET " + fieldName + " = ? WHERE empID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // bind based on runtime type
            if (fieldValue instanceof String)       stmt.setString(1, (String) fieldValue);
            else if (fieldValue instanceof Integer) stmt.setInt(1, (Integer) fieldValue);
            else if (fieldValue instanceof Double)  stmt.setDouble(1, (Double) fieldValue);
            else if (fieldValue instanceof Boolean) stmt.setBoolean(1, (Boolean) fieldValue);
            else if (fieldValue == null)            stmt.setNull(1, Types.NULL);
            else                                    stmt.setString(1, fieldValue.toString());

            stmt.setInt(2, empID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Applies a percentage raise to all employees whose salary is between min and max.
     * Uses SQL BETWEEN (inclusive) for range filtering.
     *
     * @param min   lower bound (inclusive)
     * @param max   upper bound (inclusive)
     * @param rate  raise percentage (e.g. 5.0 for +5%)
     */
    public void applySalaryRaise(double min, double max, double rate) {
        String sql = "UPDATE employees SET salary = salary * (1 + ?/100) WHERE salary BETWEEN ? AND ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, rate);
            stmt.setDouble(2, min);
            stmt.setDouble(3, max);
            int updated = stmt.executeUpdate();
            System.out.println("Applied raise to " + updated + " employees.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes an employee by empID.
     * @param empID primary key
     * @return true if at least one row was deleted
     */
    public boolean deleteEmployee(int empID) {
        String sql = "DELETE FROM employees WHERE empID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
