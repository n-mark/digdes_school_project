package ru.digdes.school.dto.employee;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.RoleInSystem;

@Getter
@Setter
@NoArgsConstructor
public class SystemRolePatchDto {
    private Long id;
    private RoleInSystem roleInSystem;

    public SystemRolePatchDto(Employee employee) {
        this.id = employee.getId();
        this.roleInSystem = employee.getRoleInSystem();
    }
}
