package ru.digdes.school.dto.project;


import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import ru.digdes.school.model.project.ProjectStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ProjectDto {
    private Long id;
    private String projectCode;
    private String projectName;
    private String description;
    private ProjectStatus projectStatus;
}
