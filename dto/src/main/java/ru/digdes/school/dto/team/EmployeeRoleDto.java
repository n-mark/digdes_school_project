package ru.digdes.school.dto.team;

import lombok.*;
import ru.digdes.school.dto.employee.IdFullNameEmployeeDto;
import ru.digdes.school.model.team.RoleInProject;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRoleDto {
    private IdFullNameEmployeeDto employee;
    private RoleInProject roleInProject;
}
