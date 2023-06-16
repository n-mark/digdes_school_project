package ru.digdes.school.service;

import ru.digdes.school.dto.team.ProjectEmployeeRoleDto;
import ru.digdes.school.dto.team.TeamDto;

public interface ProjectEmployeeRoleService {
    ProjectEmployeeRoleDto addTeamMember(ProjectEmployeeRoleDto projectEmployeeRoleDto);

    String deleteTeamMember(Long projectId, Long employeeId);

    TeamDto getProjectTeam(Long projectId);
}
