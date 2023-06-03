package ru.digdes.school.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
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
    @Schema(description = "Идентификатор сотрудника")
    private Long id;
    @Schema(description = "Фамилия. Обязательное поле")
    private String lastName;
    @Schema(description = "Имя. Обязательное поле")
    private String name;
    @Schema(description = "Отчество")
    private String middleName;
    @Schema(description = "Позиция")
    private Position position;
    @Schema(description = "Должность")
    private JobTitle jobTitle;
    @Schema(description = "Учетная запись. Уникальное значение")
    private String account;
    @Schema(description = "Электронная почта. Уникальное значение")
    @Email
    private String email;
}
