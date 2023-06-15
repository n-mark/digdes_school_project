package ru.digdes.school.dto.task;


import jakarta.validation.constraints.Email;
import lombok.*;
import ru.digdes.school.dto.employee.IdFullNameEmployeeDto;
import ru.digdes.school.dto.project.IdNameProjectDto;
import ru.digdes.school.model.task.TaskStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class TaskDto {
    private Long id;
    private String taskName;
    private String description;
    private IdFullNameEmployeeDto responsible;
    private Integer amountOfHoursNeeded;
    private LocalDateTime deadline;
    private TaskStatus taskStatus;
    private IdFullNameEmployeeDto author;
    private LocalDateTime created;
    private LocalDateTime lastModified;
    private IdNameProjectDto project;
    @Email
    private String testEmail;
}
