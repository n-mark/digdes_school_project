package ru.digdes.school.model.team;

import lombok.*;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.project.Project;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProjectEmployeeRole {
    private Long id;
    private Project project;
    private Employee employee;
    private RoleInProject roleInProject;
}
