package ru.digdes.school.dto.team;

import lombok.*;
import ru.digdes.school.dto.project.IdNameProjectDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDto {
    private IdNameProjectDto project;
    private List<EmployeeRoleDto> team = new ArrayList<>();
}
