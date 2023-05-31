package ru.digdes.school.dto.employee;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import ru.digdes.school.dto.CanBePaged;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePaging implements CanBePaged {
    private int pageNumber = 0;
    private int pageSize = 10;
    private Sort.Direction sortDirection = Sort.Direction.ASC;
    private String sortBy = "lastName";

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public Sort.Direction getSortDirection() {
        return sortDirection;
    }

    @Override
    public String getSortBy() {
        return sortBy;
    }
}
