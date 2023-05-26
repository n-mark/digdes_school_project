package ru.digdes.school.model.team;


import jakarta.persistence.*;
import lombok.*;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.project.Project;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "project_employee_role")
@ToString
public class ProjectEmployeeRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleInProject roleInProject;
}
