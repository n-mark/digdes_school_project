package ru.digdes.school.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.digdes.school.dao.repository.EmployeeRepository;
import ru.digdes.school.dao.repository.ProjectEmployeeRoleRepository;
import ru.digdes.school.dao.repository.ProjectRepository;
import ru.digdes.school.dto.employee.IdFullNameEmployeeDto;
import ru.digdes.school.dto.project.IdNameProjectDto;
import ru.digdes.school.dto.team.EmployeeRoleDto;
import ru.digdes.school.dto.team.ProjectEmployeeRoleDto;
import ru.digdes.school.dto.team.TeamDto;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.EmployeeStatus;
import ru.digdes.school.model.project.Project;
import ru.digdes.school.model.task.Task;
import ru.digdes.school.model.task.TaskStatus;
import ru.digdes.school.model.team.ProjectEmployeeRole;
import ru.digdes.school.service.ProjectEmployeeRoleService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectEmployeeRoleServiceImpl implements ProjectEmployeeRoleService {
    private final ProjectEmployeeRoleRepository projectEmployeeRoleRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final Mapper<ProjectEmployeeRole, ProjectEmployeeRoleDto> mapper;

    public ProjectEmployeeRoleServiceImpl(ProjectEmployeeRoleRepository projectEmployeeRoleRepository,
                                          ProjectRepository projectRepository,
                                          EmployeeRepository employeeRepository,
                                          Mapper<ProjectEmployeeRole, ProjectEmployeeRoleDto> mapper) {
        this.projectEmployeeRoleRepository = projectEmployeeRoleRepository;
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ProjectEmployeeRoleDto addTeamMember(ProjectEmployeeRoleDto projectEmployeeRoleDto) {
        if (projectEmployeeRoleRepository.existsByProjectIdAndEmployeeId(projectEmployeeRoleDto.getProjectId(),
                projectEmployeeRoleDto.getEmployeeId())) {
            throw new IllegalArgumentException("The employee with id = " + projectEmployeeRoleDto.getEmployeeId() +
                    " is already present on the project with id = " + projectEmployeeRoleDto.getProjectId());
        }

        if (!projectRepository.existsById(projectEmployeeRoleDto.getProjectId())) {  //TODO: дублируются проверки
            throw new IllegalArgumentException("The project with id = " + projectEmployeeRoleDto.getProjectId() +
                    " doesn't exist");
        }
        if (!employeeRepository.existsById(projectEmployeeRoleDto.getEmployeeId())) {
            throw new IllegalArgumentException("The employee with id = " + projectEmployeeRoleDto.getEmployeeId() +
                    " doesn't exist");
        }

        if(projectEmployeeRoleDto.getRoleInProject() == null) {
            throw new IllegalArgumentException("The role on the project cannot be null");
        }

        Employee employee = employeeRepository.getReferenceById(projectEmployeeRoleDto.getEmployeeId());
        if (employee.getStatus().equals(EmployeeStatus.DELETED)) {
            throw new IllegalArgumentException("Can't set employee with id = " + projectEmployeeRoleDto.getEmployeeId() +
                    " on the project cus employee status is 'DELETED'");
        }

        ProjectEmployeeRole projectEmployeeRole = mapper.dtoToModel(projectEmployeeRoleDto);

        return mapper.modelToDto(projectEmployeeRoleRepository.save(projectEmployeeRole));
    }

    @Override
    @Transactional
    public String deleteTeamMember(Long projectId, Long employeeId) {
        if (!projectRepository.existsById(projectId)) { //TODO: дублируются проверки
            throw new IllegalArgumentException("The project with id = " + projectId +
                    " doesn't exist");
        }
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("The employee with id = " + employeeId +
                    " doesn't exist");
        }

        if (!projectEmployeeRoleRepository.existsByProjectIdAndEmployeeId(projectId, employeeId)) {
            throw new IllegalArgumentException("Can't find corresponding project-employee relation.");
        }

        Project project = projectRepository.getReferenceById(projectId);
        List<Long> unclosedTasks = project.getTasks().stream()
                .filter(task -> task.getResponsible().getId().equals(employeeId))
                .filter(task -> task.getTaskStatus().compareTo(TaskStatus.CLOSED) < 0)
                .map(Task::getId)
                .toList();

        if (unclosedTasks.size() > 0) {
            throw new IllegalArgumentException("Can't remove employee with id = " + employeeId +
                    " from the project with id = " + projectId + " because the employee has unclosed task(s): " +
                    unclosedTasks);
        }

        ProjectEmployeeRole projectEmployeeRole =
                projectEmployeeRoleRepository.findByProjectIdAndEmployeeId(projectId, employeeId);

        projectEmployeeRoleRepository.delete(projectEmployeeRole);

        return "The employee with id = " + employeeId +
                " has been successfully removed from the project with id = " + projectId;
    }

    @Override
    public TeamDto getProjectTeam(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("The project with id = " + projectId +
                    " doesn't exist");
        }
        TeamDto teamDto = new TeamDto();
        Project project = projectRepository
                .getReferenceById(projectId);
        List<ProjectEmployeeRole> projectEmployeeRole = projectEmployeeRoleRepository
                .findByProjectId(projectId);
        IdNameProjectDto idNameProjectDto = new IdNameProjectDto(project.getId(), project.getProjectName());
        teamDto.setProject(idNameProjectDto);

        teamDto.setTeam(project.getTeam().stream()
                .map(employee -> {
                   IdFullNameEmployeeDto employeeDto = new IdFullNameEmployeeDto(
                            employee.getId(), employee.getLastName(), employee.getName());
                    EmployeeRoleDto employeeRoleDto = new EmployeeRoleDto();
                    employeeRoleDto.setEmployee(employeeDto);
                    employeeRoleDto.setRoleInProject(projectEmployeeRole.stream().filter(projectEmployeeRole1 ->
                                    projectEmployeeRole1.getEmployeeId().equals(employee.getId())).toList()
                            .get(0).getRoleInProject());
                    return employeeRoleDto;
                })
                .collect(Collectors.toList()));

        return teamDto;
    }
}
