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
import ru.digdes.school.model.project.Project;
import ru.digdes.school.model.project.ProjectStatus;
import ru.digdes.school.model.task.Task;
import ru.digdes.school.model.task.TaskStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskRepositoryTest {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ProjectRepository projectRepository;

    private final String testName = "TestName";

    private Task task;
    private Project project;

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
    void add() {
        project = Project.builder()
                .projectCode(testName)
                .projectName(testName)
                .projectStatus(ProjectStatus.DRAFT)
                .build();

        project = projectRepository.save(project);

        task = Task.builder().
        taskName(testName)
                .taskStatus(TaskStatus.NEW)
                .amountOfHoursNeeded(2)
                .deadline(LocalDateTime.now().plus(10, ChronoUnit.DAYS))
                .createdBy(new Employee())
                .createdWhen(LocalDateTime.now())
                .lastModifiedWhen(LocalDateTime.now())
                .project(project)
                .build();
        task = taskRepository.save(task);
    }

    @AfterEach
    void delete() {
        taskRepository.delete(task);
    }

    @Test
    void canSaveAndReadProperly() {
        Optional<Task> returnedTask = taskRepository.findById(task.getId());
        Assertions.assertTrue(returnedTask.isPresent());
        boolean isEqual = task.equals(returnedTask.get());
        Assertions.assertTrue(isEqual);
    }

    @Test
    void canUpdateProperly() {
        final String updatedDescription = "UpdatedTestName";
        Optional<Task> returnedTask = taskRepository.findById(task.getId());
        Assertions.assertTrue(returnedTask.isPresent());
        Task toUpdate = returnedTask.get();
        toUpdate.setDescription(updatedDescription);
        taskRepository.save(toUpdate);
        Optional<Task> returned = taskRepository.findById(task.getId());
        Assertions.assertTrue(returned.isPresent());
        Assertions.assertEquals(updatedDescription, returned.get().getDescription());
    }

    @Test
    void canDeleteProperly() {
        taskRepository.deleteById(task.getId());
        Optional<Task> employeeReturn = taskRepository.findById(task.getId());

        Assertions.assertTrue(employeeReturn.isEmpty());
    }

}
