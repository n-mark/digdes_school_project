package ru.digdes.school.dto.team;

import lombok.*;
import ru.digdes.school.dto.employee.IdFullNameEmployeeDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRoleDto {
    private IdFullNameEmployeeDto employee;
    private String roleInProject;
}
