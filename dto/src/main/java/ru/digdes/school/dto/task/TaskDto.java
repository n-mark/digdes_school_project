package ru.digdes.school.dto.task;


import lombok.*;
import ru.digdes.school.dto.employee.IdFullNameEmployeeDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {
    private Long id;
    private String taskName;
    private String description;
    private IdFullNameEmployeeDto responsible;
    private Integer amountOfHoursNeeded;
    private LocalDateTime deadline;
}
