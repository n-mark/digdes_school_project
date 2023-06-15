package ru.digdes.school.dao.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import ru.digdes.school.model.project.Project;
import ru.digdes.school.model.project.ProjectStatus;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProjectRepositoryTest {
    @Autowired
    private ProjectRepository projectRepository;
    private final String testName = "TestName";
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
    }

    @AfterEach
    void delete() {
        projectRepository.delete(project);
    }

    @Test
    void canSaveAndReadProperly() {
        Optional<Project> returnedProject = projectRepository.findById(project.getId());
        Assertions.assertTrue(returnedProject.isPresent());
        boolean isEqual = project.equals(returnedProject.get());
        Assertions.assertTrue(isEqual);
    }

    @Test
    void canUpdateProperly() {
        final String updatedDescription = "UpdatedTestName";
        Optional<Project> returnedProject = projectRepository.findById(project.getId());
        Assertions.assertTrue(returnedProject.isPresent());
        Project toUpdate = returnedProject.get();
        toUpdate.setDescription(updatedDescription);
        projectRepository.save(toUpdate);
        Optional<Project> returned = projectRepository.findById(project.getId());
        Assertions.assertTrue(returned.isPresent());
        Assertions.assertEquals(updatedDescription, returned.get().getDescription());
    }

    @Test
    void canDeleteProperly() {
        projectRepository.deleteById(project.getId());
        Optional<Project> projectReturn = projectRepository.findById(project.getId());

        Assertions.assertTrue(projectReturn.isEmpty());
    }

}
