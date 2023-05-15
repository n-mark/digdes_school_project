package ru.digdes.school.dto.employee;

import lombok.*;

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
    private String position;
    private String jobTitle;
    private String account;
    private String email;
}
