package ru.digdes.school.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.digdes.school.dao.repository.EmployeeRepository;
import ru.digdes.school.dto.task.TaskDto;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.security.UserDetailsImpl;

@Component
public class GetAuthenticatedService {
    private final EmployeeRepository employeeRepository;

    public GetAuthenticatedService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public UserDetailsImpl checkTheCurrentAuthenticated(TaskDto taskDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Employee createdBy = employeeRepository.getReferenceById(userDetails.getId());
        if (createdBy.getProjects().stream().noneMatch(project -> project.getId() == taskDto.getProject().getId())) {
            throw new IllegalArgumentException("You're not allowed to add/modify a task on the project with id = " +
                    taskDto.getProject().getId() + " because you're not the participant of this project");
        }
        return userDetails;
    }
}
