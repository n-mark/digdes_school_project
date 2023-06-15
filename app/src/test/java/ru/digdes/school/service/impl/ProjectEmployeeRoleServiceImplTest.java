package ru.digdes.school.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.digdes.school.dao.repository.EmployeeRepository;
import ru.digdes.school.dao.repository.ProjectEmployeeRoleRepository;
import ru.digdes.school.dao.repository.ProjectRepository;
import ru.digdes.school.dto.employee.EmployeeDto;
import ru.digdes.school.dto.project.ProjectDto;
import ru.digdes.school.dto.team.ProjectEmployeeRoleDto;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.mapping.impl.ProjectEmployeeRoleMapperImpl;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.EmployeeStatus;
import ru.digdes.school.model.project.Project;
import ru.digdes.school.model.project.ProjectStatus;
import ru.digdes.school.model.team.ProjectEmployeeRole;
import ru.digdes.school.model.team.RoleInProject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectEmployeeRoleServiceImplTest {
    private static final String TEST_VALUE = "TestValue";
    @Mock
    private ProjectEmployeeRoleRepository projectEmployeeRoleRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private Mapper<ProjectEmployeeRole, ProjectEmployeeRoleDto> mapper;
    @InjectMocks
    private ProjectEmployeeRoleServiceImpl projectEmployeeRoleService;
    private Employee employee;
    private EmployeeDto employeeDto;
    private Project project;
    private ProjectDto projectDto;
    private ProjectEmployeeRole projectEmployeeRole;
    private ProjectEmployeeRoleDto projectEmployeeRoleDto;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id(2L)
                .lastName("TestLastName")
                .name("TestName")
                .status(EmployeeStatus.ACTIVE)
                .account("TestAccount")
                .build();

        employeeDto = EmployeeDto.builder()
                .id(2L)
                .lastName("TestLastName")
                .name("TestName")
                .account("TestAccount")
                .build();

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

        projectEmployeeRole = ProjectEmployeeRole.builder()
                .id(1L)
                .projectId(project.getId())
                .employeeId(employee.getId())
                .roleInProject(RoleInProject.TESTER)
                .build();

        projectEmployeeRoleDto = ProjectEmployeeRoleDto.builder()
                .id(1L)
                .projectId(project.getId())
                .employeeId(employee.getId())
                .roleInProject(RoleInProject.TESTER)
                .build();
    }

    @Test
    void CanAddTeamMember() {
        when(projectEmployeeRoleRepository.existsByProjectIdAndEmployeeId(any(Long.class), any(Long.class)))
                .thenReturn(false);
        when(projectRepository.existsById(any(Long.class))).thenReturn(true);
        when(employeeRepository.existsById(any(Long.class))).thenReturn(true);
        when(employeeRepository.getReferenceById(any(Long.class))).thenReturn(employee);
        when(projectEmployeeRoleRepository.save(any(ProjectEmployeeRole.class))).thenReturn(projectEmployeeRole);

        when(mapper.dtoToModel(any(ProjectEmployeeRoleDto.class))).thenReturn(projectEmployeeRole);
        when(mapper.modelToDto(any(ProjectEmployeeRole.class))).thenReturn(projectEmployeeRoleDto);

        ProjectEmployeeRoleDto returned = projectEmployeeRoleService.addTeamMember(projectEmployeeRoleDto);

        Assertions.assertThat(returned).isEqualTo(projectEmployeeRoleDto);
    }
}
