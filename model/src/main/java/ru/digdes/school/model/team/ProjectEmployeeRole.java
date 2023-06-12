package ru.digdes.school.model.team;


import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "project_id")
    private Long projectId;
    @Column(name = "employee_id")
    private Long employeeId;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleInProject roleInProject;
}
