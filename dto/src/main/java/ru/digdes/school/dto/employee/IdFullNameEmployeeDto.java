package ru.digdes.school.dto.employee;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdFullNameEmployeeDto {
    private Long id;
    private String lastName;
    private String name;
}
