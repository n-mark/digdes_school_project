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
import ru.digdes.school.dto.task.*;
import ru.digdes.school.email.EmailMessage;
import ru.digdes.school.email.EmailService;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.EmployeeStatus;
import ru.digdes.school.model.task.Task;
import ru.digdes.school.model.task.TaskStatus;
import ru.digdes.school.security.UserDetailsImpl;
import ru.digdes.school.service.BasicService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl implements BasicService<TaskDto> {
    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final Mapper<Task, TaskDto> mapper;
    private final SpecificationFiltering<Task> specificationFiltering;
    private final EmailService emailService;
    private final GetAuthenticatedService getAuthenticatedService;

    public TaskServiceImpl(EmployeeRepository employeeRepository,
                           TaskRepository taskRepository,
                           ProjectRepository projectRepository,
                           Mapper<Task, TaskDto> mapper,
                           SpecificationFiltering<Task> specificationFiltering,
                           EmailService emailService,
                           GetAuthenticatedService getAuthenticatedService) {
        this.employeeRepository = employeeRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.mapper = mapper;
        this.specificationFiltering = specificationFiltering;
        this.emailService = emailService;
        this.getAuthenticatedService = getAuthenticatedService;
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

        UserDetailsImpl userDetails = getAuthenticatedService.checkTheCurrentAuthenticated(createFrom);

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
        sendEmailIfExists(createFrom, task);

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
        UserDetailsImpl userDetails = getAuthenticatedService.checkTheCurrentAuthenticated(mapper.modelToDto(task));

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

        TaskDto toReturn = mapper.modelToDto(taskRepository.save(task));
        sendEmailIfExists(updateFrom, task);

        return toReturn;
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
        getAuthenticatedService.checkTheCurrentAuthenticated(mapper.modelToDto(task));

        if (task.getTaskStatus().compareTo(changeTaskStateDto.getTaskStatus()) >= 0) {
            throw new IllegalArgumentException("Wrong assignment. The task with status '" + task.getTaskStatus() +
                    "' cannot be set to status '" + changeTaskStateDto.getTaskStatus() + "'. The proper project status " +
                    "sequence is 'NEW' -> 'IN_WORK' -> 'FINISHED' -> 'CLOSED'");
        }

        task.setTaskStatus(changeTaskStateDto.getTaskStatus());
        taskRepository.save(task);
        return "The task with id = " + task.getId() + " status were successfully changed to " + task.getTaskStatus();
    }

    @Transactional
    public String linkTasks(Long dependent, Long dependsOn) {
        if (!taskRepository.existsById(dependent)) {
            throw new IllegalArgumentException("The task with id = " + dependent + " doesn't exist");
        }
        if (!taskRepository.existsById(dependsOn)) {
            throw new IllegalArgumentException("The task with id = " + dependsOn + " doesn't exist");
        }

        if (dependent == dependsOn) {
            throw new IllegalArgumentException("Can't link the task to itself");
        }

        Task dependentTask = taskRepository.getReferenceById(dependent);
        Task dependsOnTask = taskRepository.getReferenceById(dependsOn);

        if (dependentTask.getProject().getId() != dependsOnTask.getProject().getId()) {
            throw new IllegalArgumentException("Can't link the tasks because they don't belong to the same project");
        }

        dependsOnTask.getDependentTasks().add(dependentTask);
        dependentTask.getDependsOn().add(dependsOnTask);

        return "The tasks with id = " + dependent + " and id = " + dependsOn + " has been linked successfully";
    }

    public TaskDependencyDto getAllRelatedTasks(Long taskId, boolean allParents, boolean allChildren) {
        if (!taskRepository.existsById(taskId)) {
            throw new IllegalArgumentException("The task with id = " + taskId + " doesn't exist");
        }

        TaskDependencyDto td = new TaskDependencyDto();
        Task task = taskRepository.getReferenceById(taskId);
        td.setTask(new IdNameTaskDto(task.getId(), task.getTaskName()));

        if (allParents) {
            List<Task> dependsOn = getAllParents(taskId);
            td.setParents(dependsOn.stream()
                    .map(t -> new IdNameTaskDto(t.getId(), t.getTaskName()))
                    .toList());
        } else {
            td.setParents(task.getDependsOn().stream()
                    .map(t -> new IdNameTaskDto(t.getId(), t.getTaskName()))
                    .toList());
        }

        if (allChildren) {
            List<Task> dependentTasks = getAllChildren(taskId);
            td.setChildren(dependentTasks.stream()
                    .map(t -> new IdNameTaskDto(t.getId(), t.getTaskName()))
                    .toList());
        } else {
            td.setChildren(task.getDependentTasks().stream()
                    .map(t -> new IdNameTaskDto(t.getId(), t.getTaskName()))
                    .toList());
        }

        return td;
    }

    private List<Task> getAllChildren(Long id) {
        List<Task> children = new ArrayList<>();
        Task task = taskRepository.getReferenceById(id);
        children.addAll(task.getDependentTasks());

        List<Task> subChildren = new ArrayList<>();
        for (Task task1 : children) {
            subChildren.addAll(getAllChildren(task1.getId()));
        }
        children.addAll(subChildren);

        return children;
    }


    private List<Task> getAllParents(Long id) {
        List<Task> parents = new ArrayList<>();
        Task task = taskRepository.getReferenceById(id);
        parents.addAll(task.getDependsOn());

        List<Task> subParents = new ArrayList<>();
        for (Task task1 : parents) {
            subParents.addAll(getAllParents(task1.getId()));
        }
        parents.addAll(subParents);

        return parents;
    }

//    private UserDetailsImpl checkTheCurrentAuthenticated(TaskDto taskDto) {
//        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
//                .getPrincipal();
//        Employee createdBy = employeeRepository.getReferenceById(userDetails.getId());
//        if (createdBy.getProjects().stream().noneMatch(project -> project.getId() == taskDto.getProject().getId())) {
//            throw new IllegalArgumentException("You're not allowed to add/modify a task on the project with id = " +
//                    taskDto.getProject().getId() + " because you're not the participant of this project");
//        }
//        return userDetails;
//    }

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

    private void sendEmailIfExists(TaskDto taskDto, Task task) {
        if (taskDto.getResponsible() != null) {
            Employee employee = employeeRepository.getReferenceById(taskDto.getResponsible().getId());
            if (employee.getEmail() != null) {
                EmailMessage emailMessage = EmailMessage.builder()
                        .employeeName(employee.getName())
                        .email(employee.getEmail())
                        .subject("Новая задача")
                        .message("")
                        .taskName(task.getTaskName())
                        .projectName(task.getProject().getProjectName())
                        .deadlineDate(task.getDeadline().toLocalDate().toString())
                        .deadlineTime(task.getDeadline().toLocalTime().truncatedTo(ChronoUnit.MINUTES).toString())
                        .build();
                if (taskDto.getTestEmail() != null) {
                    String employeeEmail = emailMessage.getEmail();
                    emailMessage.setEmail(taskDto.getTestEmail());
                    emailMessage.setSubject("Test email sending");
                    emailMessage.setMessage("(This is a test email sending for " + employeeEmail + ")");
                }
                emailService.sendEmailAsync(emailMessage);
            }
        }
    }

}
