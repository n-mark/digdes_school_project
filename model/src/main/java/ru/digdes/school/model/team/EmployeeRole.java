package ru.digdes.school.model.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.digdes.school.model.employee.Employee;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRole {
    private Employee employee;
    private RoleInProject roleInProject;
}
