package ru.digdes.school.model.employee;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Employee {
    private Long id;
    private String lastName;
    private String name;
    private String middleName;
    private Position position;
    private JobTitle jobTitle;
    private String account;
    private String email;
    private EmployeeStatus status;
}
