package ru.digdes.school.model.project;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Project {
    private Long id;
    private String projectCode;
    private String name;
    private String description;
    private ProjectStatus projectStatus;
}
