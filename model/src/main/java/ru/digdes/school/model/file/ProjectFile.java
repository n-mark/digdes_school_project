package ru.digdes.school.model.file;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import ru.digdes.school.model.project.Project;

@Entity
@Table(name = "project_file")
@Getter
@Setter
public class ProjectFile extends File {
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
