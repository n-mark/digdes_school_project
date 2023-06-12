package ru.digdes.school.model.task;


import jakarta.persistence.*;
import lombok.*;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.project.Project;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String taskName;
    private String description;
    @ManyToOne
    private Employee responsible;
    private Integer amountOfHoursNeeded;
    private LocalDateTime deadline;
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;
    @OneToOne
    private Employee createdBy;
    private LocalDateTime createdWhen;
    private LocalDateTime lastModifiedWhen;

    @ManyToMany
    @JoinTable(
            name = "task_dependency",
            joinColumns = @JoinColumn(name = "dependent_task_id"),
            inverseJoinColumns = @JoinColumn(name = "depends_on_id")
    )
    private List<Task> dependsOn;

    @ManyToMany(mappedBy = "dependsOn")
    private List<Task> dependentTasks;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
