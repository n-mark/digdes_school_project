package ru.digdes.school.dto.task;

import lombok.Getter;
import lombok.Setter;
import ru.digdes.school.dto.Stateable;
import ru.digdes.school.model.task.TaskStatus;

@Getter
@Setter
public class ChangeTaskStateDto implements Stateable {
    private Long id;
    private TaskStatus taskStatus;
}
