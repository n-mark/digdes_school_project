package ru.digdes.school.dto.employee;

import lombok.Getter;
import lombok.Setter;
import ru.digdes.school.dto.Stateable;

@Getter
@Setter
public class DeleteEmployeeDto implements Stateable {
    private Long id;
}
