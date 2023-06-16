package ru.digdes.school.dto.team;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.digdes.school.model.team.RoleInProject;

@Getter
@Setter
@Builder
public class ProjectEmployeeRoleDto {
    private Long id;
    private Long projectId;
    private Long employeeId;
    private RoleInProject roleInProject;
}
