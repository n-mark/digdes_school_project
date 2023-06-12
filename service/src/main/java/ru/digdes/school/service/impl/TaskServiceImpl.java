package ru.digdes.school.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.digdes.school.dao.repository.EmployeeRepository;
import ru.digdes.school.dao.repository.ProjectRepository;
import ru.digdes.school.dao.repository.TaskRepository;
import ru.digdes.school.dao.repository.specifications.SpecificationFiltering;
import ru.digdes.school.dto.CanDoFiltering;
import ru.digdes.school.dto.CanDoPaging;
import ru.digdes.school.dto.Stateable;
import ru.digdes.school.dto.employee.IdFullNameEmployeeDto;
import ru.digdes.school.dto.project.IdNameProjectDto;
import ru.digdes.school.dto.task.ChangeTaskStateDto;
import ru.digdes.school.dto.task.TaskDto;
import ru.digdes.school.dto.task.TaskFilterObject;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.EmployeeStatus;
import ru.digdes.school.model.task.Task;
import ru.digdes.school.model.task.TaskStatus;
import ru.digdes.school.security.UserDetailsImpl;
import ru.digdes.school.service.BasicService;

import java.time.LocalDateTime;

@Service
public class TaskServiceImpl implements BasicService<TaskDto> {
    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final Mapper<Task, TaskDto> mapper;
    private final SpecificationFiltering<Task> specificationFiltering;

    public TaskServiceImpl(EmployeeRepository employeeRepository,
                           TaskRepository taskRepository,
                           ProjectRepository projectRepository,
                           Mapper<Task, TaskDto> mapper,
                           SpecificationFiltering<Task> specificationFiltering) {
        this.employeeRepository = employeeRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.mapper = mapper;
        this.specificationFiltering = specificationFiltering;
    }

    @Override
    @Transactional
    public TaskDto create(TaskDto createFrom) {
        if (createFrom.getProject() == null || createFrom.getProject().getId() == null) {
            throw new IllegalArgumentException("Project id cannot be null");
        }

        if (!projectRepository.existsById(createFrom.getProject().getId())) {
            throw new IllegalArgumentException("Can't attach the task to the project with id = " +
                    createFrom.getProject().getId() + " because the project doesn't exist");
        }

        UserDetailsImpl userDetails = checkTheCurrentAuthenticated(createFrom);

        if (createFrom.getTaskName() == null) {
            throw new IllegalArgumentException("Task name cannot be null");
        }

        if (createFrom.getAmountOfHoursNeeded() < 1) {
            throw new IllegalArgumentException("Amount of hours needed cannot be less than 1");
        }

        if (createFrom.getResponsible() != null) {
            checkResponsibleEmployee(createFrom);
        }

        if (LocalDateTime.now().plusHours(createFrom.getAmountOfHoursNeeded()).isAfter(createFrom.getDeadline())) {
            throw new IllegalArgumentException("Wrong timing. Consider to decrease the amount of hours needed" +
                    " or to increase the deadline date");
        }

        createFrom.setTaskStatus(TaskStatus.NEW);
        createFrom.setAuthor(IdFullNameEmployeeDto.builder()
                .id(userDetails.getId())
                .build());
        createFrom.setCreated(LocalDateTime.now());
        createFrom.setLastModified(LocalDateTime.now());

        Task task = mapper.dtoToModel(createFrom);

        TaskDto taskDto = mapper.modelToDto(taskRepository.save(task));

        //TODO: проверить, есть ли email у установленного исполнителем сотрудника. Если да - отправить уведомление.
        System.out.println();
        return taskDto;
    }

    @Override
    @Transactional
    public TaskDto update(TaskDto updateFrom) {
        if (!taskRepository.existsById(updateFrom.getId())) {
            throw new IllegalArgumentException("Can't perform an update. The task with id = "
                    + updateFrom.getId() + " doesn't exist.");
        }

        Task task = taskRepository.getReferenceById(updateFrom.getId());
        updateFrom.setProject(IdNameProjectDto.builder().id(task.getProject().getId()).build());
        UserDetailsImpl userDetails = checkTheCurrentAuthenticated(mapper.modelToDto(task));

        if (updateFrom.getResponsible() != null) {
            checkResponsibleEmployee(updateFrom);
        }

        if (updateFrom.getAmountOfHoursNeeded() != null && updateFrom.getAmountOfHoursNeeded() < 1) {
            throw new IllegalArgumentException("Amount of hours needed cannot be less than 1");
        }

        updateFrom.setAuthor(IdFullNameEmployeeDto.builder()
                .id(userDetails.getId())
                .build());
        updateFrom.setLastModified(LocalDateTime.now());
        mapper.updateMerge(task, updateFrom);

        if (task.getCreatedWhen().plusHours(task.getAmountOfHoursNeeded()).isAfter(task.getDeadline())) {
            throw new IllegalArgumentException("Wrong timing. Consider to decrease the amount of hours needed" +
                    " or to increase the deadline date");
        }

        //TODO: проверить, есть ли email у установленного исполнителем сотрудника. Если да - отправить уведомление.
        return mapper.modelToDto(taskRepository.save(task));
    }

    @Override
    public Page<TaskDto> search(CanDoPaging pagingObject, CanDoFiltering filteringObject) {
        TaskFilterObject taskFilterObject = (TaskFilterObject) filteringObject;
        String[] keyWords = taskFilterObject.getSearchString().orElse("").split(" ");

        Pageable pageable = PageRequest.of(pagingObject.getPageNumber(), pagingObject.getPageSize());

        Page<Task> allFound = taskRepository.findAll(
                Specification.where(specificationFiltering.withAllKeywords(keyWords))
                        .and(specificationFiltering.withFilters(filteringObject))
                        .and(specificationFiltering.setSorting(pagingObject.getSortDirection(), pagingObject.getSortBy())),
                pageable
        );

        return allFound.map(mapper::modelToDto);
    }

    @Override
    @Transactional
    public String changeState(Stateable changeStateObject) {
        ChangeTaskStateDto changeTaskStateDto = (ChangeTaskStateDto) changeStateObject;
        if (!taskRepository.existsById(changeTaskStateDto.getId())) {
            throw new EntityNotFoundException("The task with id = " + changeTaskStateDto.getId() + " doesn't exist");
        }

        Task task = taskRepository.getReferenceById(changeTaskStateDto.getId());
        checkTheCurrentAuthenticated(mapper.modelToDto(task));

        if (task.getTaskStatus().compareTo(changeTaskStateDto.getTaskStatus()) >= 0) {
            throw new IllegalArgumentException("Wrong assignment. The task with status '" + task.getTaskStatus() +
                    "' cannot be set to status '" + changeTaskStateDto.getTaskStatus() + "'. The proper project status " +
                    "sequence is 'NEW' -> 'IN_WORK' -> 'FINISHED' -> 'CLOSED'");
        }

        task.setTaskStatus(changeTaskStateDto.getTaskStatus());
        taskRepository.save(task);
        return "The task with id = " + task.getId() + " status were successfully changed to " + task.getTaskStatus();
    }

    private UserDetailsImpl checkTheCurrentAuthenticated(TaskDto taskDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Employee createdBy = employeeRepository.getReferenceById(userDetails.getId());
        if (createdBy.getProjects().stream().noneMatch(project -> project.getId() == taskDto.getProject().getId())) {
            throw new IllegalArgumentException("You're not allowed to add/modify a task on the project with id = " +
                    taskDto.getProject().getId() + " because you're not the participant of this project");
        }
        return userDetails;
    }

    private void checkResponsibleEmployee(TaskDto taskDto) {
        if (taskDto.getResponsible().getId() != null &&
                !employeeRepository.existsById(taskDto.getResponsible().getId())) {
            throw new IllegalArgumentException("Can't set responsible employee because employee with id = " +
                    taskDto.getResponsible().getId() + " doesn't exist");
        }
        Employee responsible = employeeRepository.getReferenceById(taskDto.getResponsible().getId());
        if (responsible.getStatus().equals(EmployeeStatus.DELETED)) {
            throw new IllegalArgumentException("Can't set the responsible employee because employee with id = " +
                    responsible.getId() + " status is 'DELETED'");
        }
        if (responsible.getProjects().stream().noneMatch(project -> project.getId() == taskDto.getProject().getId())) {
            throw new IllegalArgumentException("Can't set the responsible employee for the task " +
                    "because he is not a participant of the project with id = " + taskDto.getProject().getId());
        }
    }
}
