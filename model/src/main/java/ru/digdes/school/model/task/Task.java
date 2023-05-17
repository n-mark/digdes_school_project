package ru.digdes.school.model.task;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.digdes.school.model.employee.Employee;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Task {
    private Long id;
    private String taskName;
    private String description;
    private Employee responsible;
    private Integer amountOfHoursNeeded;
    private LocalDateTime deadline;
    private TaskStatus taskStatus;
    private Employee createdBy;
    private LocalDateTime createdWhen;
    private LocalDateTime lastModifiedWhen;
}
