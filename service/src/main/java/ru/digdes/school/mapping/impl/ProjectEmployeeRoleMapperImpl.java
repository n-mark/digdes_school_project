package ru.digdes.school.mapping.impl;

import org.springframework.stereotype.Component;
import ru.digdes.school.dto.team.ProjectEmployeeRoleDto;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.model.team.ProjectEmployeeRole;

@Component
public class ProjectEmployeeRoleMapperImpl implements Mapper<ProjectEmployeeRole, ProjectEmployeeRoleDto> {
    @Override
    public ProjectEmployeeRoleDto modelToDto(ProjectEmployeeRole projectEmployeeRole) {
        return ProjectEmployeeRoleDto.builder()
                .id(projectEmployeeRole.getId())
                .projectId(projectEmployeeRole.getProjectId())
                .employeeId(projectEmployeeRole.getEmployeeId())
                .roleInProject(projectEmployeeRole.getRoleInProject())
                .build();
    }

    @Override
    public ProjectEmployeeRole dtoToModel(ProjectEmployeeRoleDto projectEmployeeRoleDto) {
        return ProjectEmployeeRole.builder()
                .id(projectEmployeeRoleDto.getId())
                .projectId(projectEmployeeRoleDto.getProjectId())
                .employeeId(projectEmployeeRoleDto.getEmployeeId())
                .roleInProject(projectEmployeeRoleDto.getRoleInProject())
                .build();
    }

    @Override
    public void updateMerge(ProjectEmployeeRole projectEmployeeRole, ProjectEmployeeRoleDto projectEmployeeRoleDto) {

    }
}
