package com.group02.service;

import com.group02.config.DatabaseConfig;
import com.group02.model.Employee;
import org.junit.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class EmployeeServiceTest {
    private static EmployeeService service;

    @BeforeClass
    public static void setUpClass() {
        // Reset and initialize test database
        DatabaseConfig.resetConfiguration();
        DatabaseConfig.loadProperties("test-config.properties");
        DatabaseConfig.initializeDatabaseSchema();
        DatabaseConfig.initializeConnectionPool();
        service = new EmployeeServiceImpl();
    }

    @AfterClass
    public static void tearDownClass() {
        DatabaseConfig.closeDataSource();
    }

    @After
    public void cleanDatabase() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM employees");
        }
        // Refresh service cache
        service = new EmployeeServiceImpl();
    }

    @Test
    public void testAddAndSearch() {
        Employee emp = new Employee("Alice", "123123123", "Developer", "Engineering", 50000, "FullTime");
        int id = service.add(emp);
        assertTrue(id > 0);

        Optional<Employee> fetched = service.searchByID(id);
        assertTrue(fetched.isPresent());
        assertEquals("Alice", fetched.get().getName());
        assertEquals("123123123", fetched.get().getSSN());

        List<Employee> byName = service.searchByName("Alice");
        assertFalse(byName.isEmpty());

        Optional<Employee> bySSN = service.searchBySSN("123123123");
        assertTrue(bySSN.isPresent());
    }

    @Test
    public void testUpdate() {
        Employee emp = new Employee("Bob", "321321321", "Analyst", "Finance", 60000, "PartTime");
        int id = service.add(emp);
        emp.setEmpID(id);
        emp.setJobTitle("Senior Analyst");
        boolean updated = service.update(emp);
        assertTrue(updated);

        Optional<Employee> fetched = service.searchByID(id);
        assertEquals("Senior Analyst", fetched.get().getJobTitle());
    }

    @Test
    public void testDelete() {
        Employee emp = new Employee("Carol", "456456456", "Manager", "HR", 70000, "FullTime");
        int id = service.add(emp);
        boolean deleted = service.delete(id);
        assertTrue(deleted);

        assertFalse(service.searchByID(id).isPresent());
    }

    @Test
    public void testBulkSalaryRaise() {
        Employee e1 = new Employee("Dave", "111111111", "E1", "D1", 40000, "FullTime");
        Employee e2 = new Employee("Eve", "222222222", "E2", "D2", 80000, "FullTime");
        int id1 = service.add(e1);
        int id2 = service.add(e2);

        service.applySalaryRaise(0, 60000, 10);

        Optional<Employee> f1 = service.searchByID(id1);
        Optional<Employee> f2 = service.searchByID(id2);
        assertTrue(f1.isPresent());
        assertTrue(f2.isPresent());
        assertEquals(44000, f1.get().getSalary(), 0.01);
        assertEquals(80000, f2.get().getSalary(), 0.01);
    }
}
