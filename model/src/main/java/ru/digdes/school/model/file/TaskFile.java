package ru.digdes.school.model.file;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.digdes.school.model.task.Task;

@Entity
@Table(name = "task_file")
@Getter
@Setter
public class TaskFile extends File {
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}
