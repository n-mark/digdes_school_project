package ru.digdes.school.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.digdes.school.dao.repository.EmployeeRepository;
import ru.digdes.school.dao.repository.ProjectRepository;
import ru.digdes.school.dao.repository.TaskRepository;
import ru.digdes.school.dao.repository.specifications.SpecificationFiltering;
import ru.digdes.school.dto.project.IdNameProjectDto;
import ru.digdes.school.dto.task.ChangeTaskStateDto;
import ru.digdes.school.dto.task.TaskDto;
import ru.digdes.school.dto.task.TaskFilterObject;
import ru.digdes.school.dto.task.TaskPaging;
import ru.digdes.school.email.EmailService;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.project.Project;
import ru.digdes.school.model.project.ProjectStatus;
import ru.digdes.school.model.task.Task;
import ru.digdes.school.model.task.TaskStatus;
import ru.digdes.school.security.UserDetailsImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private Mapper<Task, TaskDto> mapper;
    @Mock
    private SpecificationFiltering<Task> specificationFiltering;
    @Mock
    private EmailService emailService;
    @Mock
    private GetAuthenticatedService getAuthenticatedService;
    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private TaskDto taskDto;
    private Project project;
    private final String TEST_VALUE = "TestValue";

    @BeforeEach
    void add() {
        project = projectRepository.save(project);

        task = Task.builder()
                .id(1L)
                .taskName(TEST_VALUE)
                .taskStatus(TaskStatus.NEW)
                .amountOfHoursNeeded(2)
                .deadline(LocalDateTime.now().plus(10, ChronoUnit.DAYS))
                .createdBy(new Employee())
                .createdWhen(LocalDateTime.now())
                .lastModifiedWhen(LocalDateTime.now())
                .project(Project.builder()
                        .id(1L)
                        .projectCode(TEST_VALUE)
                        .projectName(TEST_VALUE)
                        .projectStatus(ProjectStatus.DRAFT)
                        .build())
                .build();

        taskDto = TaskDto.builder()
                .taskName(TEST_VALUE)
                .taskStatus(TaskStatus.NEW)
                .amountOfHoursNeeded(2)
                .deadline(LocalDateTime.now().plus(10, ChronoUnit.DAYS))
                .project(new IdNameProjectDto(1L, TEST_VALUE))
                .build();
    }

    @Test
    void checkCreate() {
        UserDetailsImpl userDetailsMock = Mockito.mock(UserDetailsImpl.class);
        when(getAuthenticatedService.checkTheCurrentAuthenticated(any(TaskDto.class))).thenReturn(userDetailsMock);
        when(userDetailsMock.getId()).thenReturn(2L);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(projectRepository.existsById(any(Long.class))).thenReturn(true);
        when(mapper.dtoToModel(any(TaskDto.class))).thenReturn(task);
        when(mapper.modelToDto(any(Task.class))).thenReturn(taskDto);
        TaskDto returnedTaskDto = taskService.create(taskDto);
        Assertions.assertThat(returnedTaskDto).isEqualTo(taskDto);
    }

    @Test
    void checkUpdate() {
        final String UPDATED_DESCRIPTION = "TestUpdatedTaskDescription";
        Long updateId = 1L;
        Task updatedTask = Task.builder()
                .id(1L)
                .taskName(TEST_VALUE)
                .description(UPDATED_DESCRIPTION)
                .taskStatus(TaskStatus.NEW)
                .amountOfHoursNeeded(2)
                .deadline(LocalDateTime.now().plus(10, ChronoUnit.DAYS))
                .createdBy(new Employee())
                .createdWhen(LocalDateTime.now())
                .lastModifiedWhen(LocalDateTime.now())
                .project(project)
                .build();

        TaskDto updatedTaskDtoInput = TaskDto.builder()
                .id(1L)
                .taskName(TEST_VALUE)
                .taskStatus(TaskStatus.NEW)
                .amountOfHoursNeeded(2)
                .deadline(LocalDateTime.now().plus(10, ChronoUnit.DAYS))
                .project(new IdNameProjectDto(1L, TEST_VALUE))
                .build();

        when(taskRepository.existsById(updateId)).thenReturn(true);
        when(taskRepository.getReferenceById(any(Long.class))).thenReturn(task);
        UserDetailsImpl userDetailsMock = Mockito.mock(UserDetailsImpl.class);
        when(getAuthenticatedService.checkTheCurrentAuthenticated(any(TaskDto.class))).thenReturn(userDetailsMock);
        when(userDetailsMock.getId()).thenReturn(2L);
        doNothing().when(mapper).updateMerge(any(Task.class), any(TaskDto.class));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(mapper.modelToDto(any(Task.class))).thenReturn(updatedTaskDtoInput);

        TaskDto updatedTaskDtoOutput = taskService.update(updatedTaskDtoInput);

        Assertions.assertThat(updatedTaskDtoOutput).isEqualTo(updatedTaskDtoInput);
    }

    @Test
    void checkSearch() {
        TaskDto searchResultDto = TaskDto.builder()
                .id(1L)
                .taskName(TEST_VALUE)
                .taskStatus(TaskStatus.NEW)
                .amountOfHoursNeeded(2)
                .deadline(LocalDateTime.now().plus(10, ChronoUnit.DAYS))
                .project(IdNameProjectDto.builder()
                        .id(1L)
                        .name(TEST_VALUE)
                        .build())
                .build();
        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(task));
        TaskPaging taskPaging = new TaskPaging();
        TaskFilterObject taskFilterObject = new TaskFilterObject();
        taskFilterObject.setSearchString("testString");

        when(taskRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(taskPage);
        when(mapper.modelToDto(any(Task.class))).thenReturn(searchResultDto);

        Page<TaskDto> taskDtos = taskService.search(taskPaging, taskFilterObject);

        Assertions.assertThat(taskDtos.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(taskDtos.getContent().get(0)).isEqualTo(searchResultDto);
    }

        @Test
    void checkChangeState() {
        Long changeEntityId = 1L;
        ChangeTaskStateDto changeTaskStateDto = new ChangeTaskStateDto();
        changeTaskStateDto.setId(changeEntityId);
        changeTaskStateDto.setTaskStatus(TaskStatus.FINISHED);
        when(taskRepository.existsById(changeEntityId)).thenReturn(true);
        when(taskRepository.getReferenceById(changeEntityId)).thenReturn(task);

        Task withChangedState = Task.builder()
                .id(1L)
                .taskName(TEST_VALUE)
                .taskStatus(TaskStatus.NEW)
                .amountOfHoursNeeded(2)
                .deadline(LocalDateTime.now().plus(10, ChronoUnit.DAYS))
                .build();
        withChangedState.setTaskStatus(TaskStatus.FINISHED);

        when(taskRepository.save(any(Task.class))).thenReturn(withChangedState);


        Assertions.assertThat(taskService.changeState(changeTaskStateDto))
                .isEqualTo("The task with id = " + changeTaskStateDto.getId()
                        + " status were successfully changed to " + TaskStatus.FINISHED);

    }
}
