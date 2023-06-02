package ru.digdes.school.dto.employee;

import lombok.*;
import ru.digdes.school.dto.CanFilter;

import java.util.Optional;

@Setter
public class EmployeeFilterObject implements CanFilter {
    private String searchString;

    public Optional<String> getSearchString() {
        return Optional.ofNullable(searchString);
    }
}
