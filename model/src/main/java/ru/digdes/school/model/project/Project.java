package ru.digdes.school.model.project;

import jakarta.persistence.*;
import lombok.*;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.file.ProjectFile;
import ru.digdes.school.model.task.Task;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String projectCode;
    private String projectName;
    private String description;
    @Enumerated(EnumType.STRING)
    private ProjectStatus projectStatus;
    @ManyToMany
    @JoinTable(name = "project_employee_role",
                joinColumns = @JoinColumn(name = "project_id"),
                inverseJoinColumns = @JoinColumn(name = "employee_id"))
    @ToString.Exclude
    private List<Employee> team = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<Task> tasks;
    @OneToMany(mappedBy = "project")
    private List<ProjectFile> files;
}
