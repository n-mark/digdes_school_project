package ru.digdes.school.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.digdes.school.dto.CanDoFiltering;
import ru.digdes.school.model.project.ProjectStatus;
import ru.digdes.school.model.task.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskFilterObject implements CanDoFiltering {
    private String searchString;
    private List<TaskStatus> status = new ArrayList<>();
    private List<Long> responsible = new ArrayList<>();
    private List<Long> createdBy = new ArrayList<>();
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime deadlineFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime deadlineTo;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdWhenFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdWhenTo;

    public Optional<String> getSearchString() {
        return Optional.ofNullable(searchString);
    }
    public List<TaskStatus> getStatus() {
        return status;
    }

}
