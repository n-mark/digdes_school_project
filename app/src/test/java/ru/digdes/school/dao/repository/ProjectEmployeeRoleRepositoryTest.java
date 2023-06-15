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
import ru.digdes.school.model.project.Project;
import ru.digdes.school.model.project.ProjectStatus;
import ru.digdes.school.model.team.ProjectEmployeeRole;
import ru.digdes.school.model.team.RoleInProject;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProjectEmployeeRoleRepositoryTest {
    @Autowired
    private ProjectEmployeeRoleRepository projectEmployeeRoleRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    private Employee employee;
    private Project project;
    private ProjectEmployeeRole projectEmployeeRole;
    private final String TEST_VALUE = "TestValue";

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
    void setUp() {
        project = Project.builder()
                .projectCode(TEST_VALUE)
                .projectName(TEST_VALUE)
                .projectStatus(ProjectStatus.DRAFT)
                .build();

        project = projectRepository.save(project);

        employee = Employee.builder()
                .lastName(TEST_VALUE)
                .name(TEST_VALUE)
                .status(EmployeeStatus.ACTIVE)
                .build();

        employee = employeeRepository.save(employee);

        projectEmployeeRole = ProjectEmployeeRole.builder()
                .employeeId(employee.getId())
                .projectId(project.getId())
                .roleInProject(RoleInProject.TESTER)
                .build();

        projectEmployeeRole = projectEmployeeRoleRepository.save(projectEmployeeRole);
    }

    @AfterEach
    void tearDown() {
        projectEmployeeRoleRepository.delete(projectEmployeeRole);
        projectRepository.delete(project);
        employeeRepository.delete(employee);
    }

    @Test
    void canAddParticipant() {
        ProjectEmployeeRole returned = projectEmployeeRoleRepository
                .getReferenceById(projectEmployeeRole.getId());
        Assertions.assertEquals(projectEmployeeRole, returned);
    }

    @Test
    void canDeleteParticipant() {
        projectEmployeeRoleRepository.delete(projectEmployeeRole);
        Assertions.assertFalse(projectEmployeeRoleRepository
                .existsByProjectIdAndEmployeeId(project.getId(), employee.getId()));
    }

    @Test
    void canGetAll() {
        List<ProjectEmployeeRole> projectEmployeeRoleList =
                projectEmployeeRoleRepository.findAll();
        Assertions.assertTrue(
                projectEmployeeRoleList.stream()
                        .anyMatch(prEmp -> prEmp.getEmployeeId().equals(employee.getId()))
        );
    }
}
