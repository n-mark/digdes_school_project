package ru.digdes.school.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.digdes.school.dao.repository.ProjectRepository;
import ru.digdes.school.dao.repository.specifications.SpecificationFiltering;
import ru.digdes.school.dto.project.ChangeProjectStateDto;
import ru.digdes.school.dto.project.ProjectDto;
import ru.digdes.school.dto.project.ProjectFilterObject;
import ru.digdes.school.dto.project.ProjectPaging;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.mapping.impl.ProjectMapperImpl;
import ru.digdes.school.model.project.Project;
import ru.digdes.school.model.project.ProjectStatus;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private Mapper<Project, ProjectDto> mapper = new ProjectMapperImpl();
    @Mock
    SpecificationFiltering<Project> specificationFiltering;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private ProjectDto projectDto;
    private final String TEST_VALUE = "TestValue";

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .projectCode(TEST_VALUE)
                .projectName(TEST_VALUE)
                .projectStatus(ProjectStatus.DRAFT)
                .build();

        projectDto = ProjectDto.builder()
                .id(1L)
                .projectCode(TEST_VALUE)
                .projectName(TEST_VALUE)
                .projectStatus(ProjectStatus.DRAFT)
                .build();
    }

    @Test
    void checkCreate() {
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        ProjectDto returnedProjectDto = projectService.create(projectDto);
        Assertions.assertThat(returnedProjectDto).isEqualTo(projectDto);
    }

    @Test
    void checkUpdate() {
        final String UPDATED_CODE = "TestUpdatedProjectCode";
        Long updateId = 1L;
        Project updatedProject = Project.builder()
                .id(1L)
                .projectCode(UPDATED_CODE)
                .projectName(TEST_VALUE)
                .projectStatus(ProjectStatus.DRAFT)
                .build();

        ProjectDto updatedProjectDtoInput = ProjectDto.builder()
                .id(1L)
                .projectCode(UPDATED_CODE)
                .projectName(TEST_VALUE)
                .projectStatus(ProjectStatus.DRAFT)
                .build();

        when(projectRepository.existsById(updateId)).thenReturn(true);
        when(projectRepository.getReferenceById(updateId)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

        ProjectDto updatedProjectDtoOutput = projectService.update(updatedProjectDtoInput);

        Assertions.assertThat(updatedProjectDtoOutput).isEqualTo(updatedProjectDtoInput);
    }

    @Test
    void checkSearch() {
        Page<Project> projectPage = new PageImpl<>(Collections.singletonList(project));
        ProjectPaging projectPaging = new ProjectPaging();
        ProjectFilterObject projectFilterObject = new ProjectFilterObject();
        projectFilterObject.setSearchString("testString");

        when(projectRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(projectPage);

        Page<ProjectDto> projectDtos = projectService.search(projectPaging, projectFilterObject);

        Assertions.assertThat(projectDtos.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(projectDtos.getContent().get(0)).isEqualTo(projectDto);
    }

    @Test
    void checkChangeState() {
        Long changeEntityId = 1L;
        ChangeProjectStateDto changeProjectStateDto = new ChangeProjectStateDto();
        changeProjectStateDto.setId(changeEntityId);
        changeProjectStateDto.setProjectStatus(ProjectStatus.TESTING);
        when(projectRepository.existsById(changeEntityId)).thenReturn(true);
        when(projectRepository.getReferenceById(changeEntityId)).thenReturn(project);

        Project withChangedState = Project.builder()
                .projectCode(project.getProjectCode())
                .projectName(project.getProjectName())
                .build();
        withChangedState.setProjectStatus(ProjectStatus.TESTING);

        when(projectRepository.save(any(Project.class))).thenReturn(withChangedState);


        Assertions.assertThat(projectService.changeState(changeProjectStateDto))
                .isEqualTo("The project with id = " + changeProjectStateDto.getId()
                        + " status were successfully changed to " + ProjectStatus.TESTING);

    }
}
