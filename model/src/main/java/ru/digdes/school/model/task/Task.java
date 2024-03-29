package ru.digdes.school.model.task;


import jakarta.persistence.*;
import lombok.*;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.file.ProjectFile;
import ru.digdes.school.model.file.TaskFile;
import ru.digdes.school.model.project.Project;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode
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
    @EqualsAndHashCode.Exclude
    private List<Task> dependsOn;

    @ManyToMany(mappedBy = "dependsOn")
    @Column(name = "dependent_task_id")
    @EqualsAndHashCode.Exclude
    private List<Task> dependentTasks = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "project_id")
    @EqualsAndHashCode.Exclude
    private Project project;

    @OneToMany(mappedBy = "task")
    @EqualsAndHashCode.Exclude
    private List<TaskFile> files = new ArrayList<>();
}
