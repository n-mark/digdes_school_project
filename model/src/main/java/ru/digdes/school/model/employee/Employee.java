package ru.digdes.school.model.employee;

import jakarta.persistence.*;
import lombok.*;
import ru.digdes.school.model.project.Project;
import ru.digdes.school.model.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String lastName;
    private String name;
    private String middleName;
    @Enumerated(EnumType.STRING)
    private Position position;
    @Enumerated(EnumType.STRING)
    private JobTitle jobTitle;
    private String account;
    private String email;
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;
    private String password;
    @Enumerated(EnumType.STRING)
    private RoleInSystem roleInSystem;
    @ManyToMany(mappedBy = "team")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Project> projects = new ArrayList<>();
    @OneToMany(mappedBy = "responsible")
    private List<Task> tasks = new ArrayList<>();
}
