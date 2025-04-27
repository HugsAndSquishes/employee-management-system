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

/*
What the database table looks like:

CREATE TABLE IF NOT EXISTS employees (
    empID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    employeeName VARCHAR(255),
    SSN VARCHAR(9) NOT NULL UNIQUE, -- No two rows can have the same SSN value
    jobTitle VARCHAR(255),
    division VARCHAR(255),
    salary DECIMAL(10,2),
    payInfo VARCHAR(255)
);
 */

public class EmployeeManager implements Searchable {

    /**
     * Adds a new employee to the database
     * 
     * @param employee The employee object to be stored
     * @return The newly created empID of the employee or -1 if failure.
     * @throws SQLException if database access error occurs.
     * @throws SQLException if you try to add an employee with an existing SSN
     */

    public int addEmployee(Employee employee) {
        String sql = "INSERT INTO employees (employeeName, division, SSN, jobTitle, salary, payInfo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getDivision());
            stmt.setString(3, employee.getSSN());
            stmt.setString(4, employee.getJobTitle());
            stmt.setDouble(5, employee.getSalary());
            stmt.setString(6, employee.getPayInfo());

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

    /**
     * Helper function that a maps a ResultSet (table row) to a new Employee object
     * 
     * @param rs ResultSet to map
     * @return Employee object with data from the ResultSet
     * @throws SQLException if database access error occurs
     */
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmpID(rs.getInt("empID"));
        employee.setName(rs.getString("employeeName"));
        employee.setSSN(rs.getString("SSN"));
        employee.setJobTitle(rs.getString("jobTitle"));
        employee.setDivision(rs.getString("division"));
        employee.setSalary(rs.getDouble("salary"));
        employee.setPayInfo(rs.getString("payInfo"));
        return employee;
    }

    /**
     * Retrieve all employees from the database
     * 
     * @return List of all employees
     */
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (Connection conn = DatabaseUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Employee employee = mapResultSetToEmployee(rs);
                employees.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employees;
    }

    // #region Search Functions

    // TODO: add a general search function as shown in the class diagram
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Employee> searchByID(int empID) {
        String sql = "SELECT * FROM employees WHERE empID = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = mapResultSetToEmployee(rs);
                    // Returns the employee object
                    return Optional.of(employee);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Returns empty optional instead of null
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Employee> searchBySSN(String SSN) {
        String sql = "SELECT * FROM employees WHERE SSN = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, SSN);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = mapResultSetToEmployee(rs);
                    // Returns the employee object
                    return Optional.of(employee);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Returns empty optional instead of null
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Employee> searchByName(String employeeName) {
        String sql = "SELECT * FROM employees WHERE employeeName REGEXP ?";
        List<Employee> employees = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employeeName);

            try (ResultSet rs = stmt.executeQuery()) {
                // Note that this uses a while loop instead of the usual if statement because
                // this function returns a list
                while (rs.next()) {
                    Employee employee = mapResultSetToEmployee(rs);
                    employees.add(employee);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return a list of employees
        return employees;
    }
    // #endregion

    // #region Update Functions

    /**
     * Update all fields in an existing employee record
     * 
     * @param employee The employee object with updated values. The database will
     *                 get its new values frmo this
     * @return true if update successful, false otherwise
     */
    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE employees SET employeeName = ?, division = ?, SSN = ?, " +
                "jobTitle = ?, salary = ?, payInfo = ? WHERE empID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getDivision());
            stmt.setString(3, employee.getSSN());
            stmt.setString(4, employee.getJobTitle());
            stmt.setDouble(5, employee.getSalary());
            stmt.setString(6, employee.getPayInfo());
            stmt.setInt(7, employee.getEmpID());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a single field an existing employee record
     * 
     * @param empID      The employee ID
     * @param fieldName  The name of the field to update
     * @param fieldValue The new value for the field
     * @return true if update successful, false otherwise
     */
    boolean updateField(int empID, String fieldName, Object fieldValue) {
        List<String> validFields = List.of(
                "employeeName", "division", "SSN", "jobTitle", "salary", "payInfo");

        // Validate field name
        if (!validFields.contains(fieldName)) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }

        String sql = "UPDATE employees SET " + fieldName + " = ? WHERE empID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Check the fieldValues's class for the parameter type
            if (fieldValue instanceof String) {
                stmt.setString(1, (String) fieldValue);
            } else if (fieldValue instanceof Integer) {
                stmt.setInt(1, (Integer) fieldValue);
            } else if (fieldValue instanceof Double) {
                stmt.setDouble(1, (Double) fieldValue);
            } else if (fieldValue instanceof Boolean) {
                stmt.setBoolean(1, (Boolean) fieldValue);
            } else if (fieldValue == null) {
                stmt.setNull(1, java.sql.Types.NULL);
            } else {
                // Default to string representation
                stmt.setString(1, fieldValue.toString());
            }

            stmt.setInt(2, empID);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // #endregion

    /**
     * Delete an employee by their ID
     * 
     * @param empID The employee ID to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteEmployee(int empID) {
        String sql = "DELETE FROM employees WHERE empID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empID);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void applySalaryRaise(double min, double max, double rate) {
        // TODO: Finish applySalaryRaise() function
    }
}
