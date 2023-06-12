package ru.digdes.school.mapping.impl;

import org.springframework.stereotype.Component;
import ru.digdes.school.dao.repository.EmployeeRepository;
import ru.digdes.school.dao.repository.ProjectRepository;
import ru.digdes.school.dto.employee.IdFullNameEmployeeDto;
import ru.digdes.school.dto.project.IdNameProjectDto;
import ru.digdes.school.dto.task.TaskDto;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.model.task.Task;

@Component
public class TaskMapperImpl implements Mapper<Task, TaskDto> {
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;

    public TaskMapperImpl(EmployeeRepository employeeRepository,
                          ProjectRepository projectRepository) {
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public TaskDto modelToDto(Task task) {
        IdFullNameEmployeeDto responsible = new IdFullNameEmployeeDto();
        if (task.getResponsible() != null) {
            responsible.setId(task.getResponsible().getId());
            responsible.setLastName(task.getResponsible().getLastName());
            responsible.setName(task.getResponsible().getName());
        }
        IdFullNameEmployeeDto author = IdFullNameEmployeeDto.builder()
                .id(task.getCreatedBy().getId())
                .lastName(task.getCreatedBy().getLastName())
                .name(task.getCreatedBy().getName())
                .build();

        IdNameProjectDto project = IdNameProjectDto.builder()
                .id(task.getProject().getId())
                .name(task.getProject().getProjectName())
                .build();

        return TaskDto.builder()
                .id(task.getId())
                .taskName(task.getTaskName())
                .description(task.getDescription())
                .responsible(responsible)
                .amountOfHoursNeeded(task.getAmountOfHoursNeeded())
                .deadline(task.getDeadline())
                .taskStatus(task.getTaskStatus())
                .author(author)
                .created(task.getCreatedWhen())
                .lastModified(task.getLastModifiedWhen())
                .project(project)
                .build();
    }

    @Override
    public Task dtoToModel(TaskDto taskDto) {
        return Task.builder()
                .id(taskDto.getId())
                .taskName(taskDto.getTaskName())
                .description(taskDto.getDescription())
                .responsible(taskDto.getResponsible() == null ? null :
                        employeeRepository.getReferenceById(taskDto.getResponsible().getId()))
                .amountOfHoursNeeded(taskDto.getAmountOfHoursNeeded())
                .deadline(taskDto.getDeadline())
                .taskStatus(taskDto.getTaskStatus())
                .createdBy(employeeRepository.getReferenceById(taskDto.getAuthor().getId()))
                .createdWhen(taskDto.getCreated())
                .lastModifiedWhen(taskDto.getLastModified())
                .project(projectRepository.getReferenceById(taskDto.getProject().getId()))
                .build();
    }

    @Override
    public void updateMerge(Task task, TaskDto taskDto) {
        task.setTaskName(taskDto.getTaskName() == null ? task.getTaskName() : taskDto.getTaskName());
        task.setDescription(taskDto.getDescription() == null ? task.getDescription() : taskDto.getDescription());
        task.setResponsible(taskDto.getResponsible() == null ? task.getResponsible() :
                employeeRepository.getReferenceById(taskDto.getResponsible().getId()));
        task.setAmountOfHoursNeeded(taskDto.getAmountOfHoursNeeded() == null ? task.getAmountOfHoursNeeded() :
                taskDto.getAmountOfHoursNeeded());
        task.setDeadline(taskDto.getDeadline() == null ? task.getDeadline() : taskDto.getDeadline());
        task.setCreatedBy(employeeRepository.getReferenceById(taskDto.getAuthor().getId()));
        task.setLastModifiedWhen(taskDto.getLastModified());
    }
}
