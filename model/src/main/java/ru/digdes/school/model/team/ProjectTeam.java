package ru.digdes.school.model.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.digdes.school.model.project.Project;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTeam {
    private Project project;
    private Set<EmployeeRole> team = new HashSet<>();
}
