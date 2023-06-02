package ru.digdes.school.dto.employee;

import lombok.*;
import ru.digdes.school.model.employee.JobTitle;
import ru.digdes.school.model.employee.Position;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EmployeeDto {
    private Long id;
    private String lastName;
    private String name;
    private String middleName;
    private Position position;
    private JobTitle jobTitle;
    private String account;
    private String email;
}
