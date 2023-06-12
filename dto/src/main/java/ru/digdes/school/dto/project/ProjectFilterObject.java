package ru.digdes.school.dto.project;

import lombok.Setter;
import ru.digdes.school.dto.CanDoFiltering;
import ru.digdes.school.model.project.ProjectStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Setter
public class ProjectFilterObject implements CanDoFiltering {
    private String searchString;
    List<ProjectStatus> status = new ArrayList<>();

    public Optional<String> getSearchString() {
        return Optional.ofNullable(searchString);
    }

    public List<ProjectStatus> getStatus() {
        return status;
    }
}
