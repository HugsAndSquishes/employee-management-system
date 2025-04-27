package com.group02;

import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;

import com.group02.repository.EmployeeManager;
import com.group02.config.DatabaseConfig;
import com.group02.model.Employee;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @BeforeClass
    public static void setUp() {
        // Reset any previous config
        DatabaseConfig.resetConfiguration();

        // Load test configuration
        DatabaseConfig.loadProperties("test-config.properties");

        // Initialize test database schema
        DatabaseConfig.initializeDatabaseSchema();

        // Initialize connection pool with test database
        DatabaseConfig.initializeConnectionPool();

        // Clear all data for clean tests
        cleanTestDatabase();
    }

    private static void cleanTestDatabase() {
        try (Connection conn = DatabaseConfig.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM employees");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() {
        DatabaseConfig.closeDataSource();
    }

    private EmployeeManager employeeManager = new EmployeeManager();

    @After
    public void cleanupTestData() {
        try (Connection conn = DatabaseConfig.getConnection();
                Statement stmt = conn.createStatement()) {
            // Delete all test data except for any fixed reference data
            stmt.executeUpdate("DELETE FROM employees");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateEmployee() {
        Employee employee = new Employee("Jane Smith", "987654321", "Manager", "HR", 80000, "FullTime");
        int generatedId = employeeManager.addEmployee(employee);

        System.out.println("Expected EmpID: >0");
        System.out.println("Actual EmpID: " + generatedId);

        assertTrue(generatedId > 0);
    }

    @Test
    public void testReadEmployees() {
        List<Employee> employees = employeeManager.findAll();
        assertNotNull(employees);
        assertTrue(employees.size() >= 0);
    }

    @Test
    public void testUpdateEmployee() {
        // First create a new employee
        Employee employee = new Employee("Update Test", "111222333", "Analyst", "Finance", 60000, "PartTime");
        int empID = employeeManager.addEmployee(employee);

        // Update their job title
        employee.setEmpID(empID);
        employee.setJobTitle("Senior Analyst");
        boolean success = employeeManager.updateEmployee(employee);
        assertTrue(success);

        // Fetch again to check
        List<Employee> employees = employeeManager.findAll();
        Employee updatedEmployee = employees.stream()
                .filter(e -> e.getEmpID() == empID)
                .findFirst()
                .orElse(null);
        assertEquals("Senior Analyst", updatedEmployee.getJobTitle());
    }

    @Test
    public void testDeleteEmployee() {
        // Create an employee
        Employee employee = new Employee("Delete Test", "222333444", "Temp", "IT", 50000, "Contract");
        int empID = employeeManager.addEmployee(employee);

        // Delete the employee
        boolean deleted = employeeManager.deleteEmployee(empID);
        assertTrue(deleted);

        // Confirm deletion
        List<Employee> employees = employeeManager.findAll();
        boolean stillExists = employees.stream()
                .anyMatch(e -> e.getEmpID() == empID);
        assertFalse(stillExists);
    }
}
