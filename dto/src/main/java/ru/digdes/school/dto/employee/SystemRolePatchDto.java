package ru.digdes.school.dto.employee;

import lombok.*;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.RoleInSystem;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SystemRolePatchDto {
    private Long id;
    private RoleInSystem roleInSystem;

    public SystemRolePatchDto(Employee employee) {
        this.id = employee.getId();
        this.roleInSystem = employee.getRoleInSystem();
    }
}
