package ru.digdes.school.dao.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.EmployeeStatus;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;

    private final String testName = "TestName";

    private Employee employee;
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgreSQLContainer.stop();
    }

    @BeforeEach
    void addEmployee() {
        employee = Employee.builder()
                .lastName(testName)
                .name(testName)
                .account(testName)
                .status(EmployeeStatus.ACTIVE)
                .build();
        employee = employeeRepository.save(employee);
    }

    @AfterEach
    void deleteEmployee() {
        employeeRepository.delete(employee);
    }

    @Test
    void canSaveAndReadProperly() {
        Optional<Employee> returnedEmployee = employeeRepository.findByAccount(testName);
        Assertions.assertTrue(returnedEmployee.isPresent());
        boolean isEqual = employee.equals(returnedEmployee.get());
        Assertions.assertTrue(isEqual);
    }

    @Test
    void canUpdateProperly() {
        final String updatedName = "UpdatedTestName";
        Optional<Employee> returnedEmployee = employeeRepository.findByAccount(testName);
        Assertions.assertTrue(returnedEmployee.isPresent());
        Employee toUpdate = returnedEmployee.get();
        toUpdate.setName(updatedName);
        employeeRepository.save(toUpdate);
        Optional<Employee> returnedEmployee2 = employeeRepository.findByAccount(testName);
        Assertions.assertTrue(returnedEmployee2.isPresent());
        Assertions.assertEquals(updatedName, returnedEmployee2.get().getName());
    }

    @Test
    void canDeleteProperly() {
        employeeRepository.deleteById(employee.getId());
        Optional<Employee> employeeReturn = employeeRepository.findById(employee.getId());

        Assertions.assertTrue(employeeReturn.isEmpty());
    }
}
