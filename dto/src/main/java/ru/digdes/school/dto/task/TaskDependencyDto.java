package ru.digdes.school.dto.task;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDependencyDto {
    private IdNameTaskDto task;
    private List<IdNameTaskDto> parents = new ArrayList<>();
    private List<IdNameTaskDto> children = new ArrayList<>();
}