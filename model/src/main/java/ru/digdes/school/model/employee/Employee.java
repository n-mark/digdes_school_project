package ru.digdes.school.model.employee;

import jakarta.persistence.*;
import lombok.*;
import ru.digdes.school.model.project.Project;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
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
    @ManyToMany(mappedBy = "team")
    @ToString.Exclude
    private Set<Project> projects;
}
