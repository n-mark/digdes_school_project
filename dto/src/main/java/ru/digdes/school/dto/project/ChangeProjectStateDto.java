package ru.digdes.school.dto.project;

import lombok.Getter;
import lombok.Setter;
import ru.digdes.school.dto.Stateable;
import ru.digdes.school.model.project.ProjectStatus;

@Getter
@Setter
public class ChangeProjectStateDto implements Stateable {
    private Long id;
    private ProjectStatus projectStatus;
}
