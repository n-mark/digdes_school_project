package ru.digdes.school.mapping.impl;

import org.springframework.stereotype.Component;
import ru.digdes.school.dto.project.ProjectDto;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.model.project.Project;

@Component
public class ProjectMapperImpl implements Mapper<Project, ProjectDto> {
    @Override
    public ProjectDto modelToDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId())
                .projectCode(project.getProjectCode())
                .projectName(project.getProjectName())
                .description(project.getDescription())
                .projectStatus(project.getProjectStatus())
                .build();
    }

    @Override
    public Project dtoToModel(ProjectDto projectDto) {
        return Project.builder()
                .id(projectDto.getId())
                .projectName(projectDto.getProjectName())
                .description(projectDto.getDescription())
                .build();
    }

    @Override
    public void updateMerge(Project project, ProjectDto projectDto) {
        project.setProjectName(projectDto.getProjectName() == null ? project.getProjectName() : projectDto.getProjectName());
        project.setDescription(projectDto.getDescription() == null ? project.getDescription() : projectDto.getDescription());
    }
}
