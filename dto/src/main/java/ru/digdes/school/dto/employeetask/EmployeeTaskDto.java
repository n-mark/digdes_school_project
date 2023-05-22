package ru.digdes.school.dto.employeetask;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EmployeeTaskDto {
    private String lastName;
    private String name;
    private String taskName;
    private String description;
}
