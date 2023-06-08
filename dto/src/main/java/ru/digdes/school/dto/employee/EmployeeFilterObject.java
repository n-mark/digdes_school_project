package ru.digdes.school.dto.employee;

import lombok.*;
import ru.digdes.school.dto.CanDoFiltering;

import java.util.Optional;

@Setter
public class EmployeeFilterObject implements CanDoFiltering {
    private String searchString;

    public Optional<String> getSearchString() {
        return Optional.ofNullable(searchString);
    }
}
