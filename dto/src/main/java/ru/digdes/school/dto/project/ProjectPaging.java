package ru.digdes.school.dto.project;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import ru.digdes.school.dto.CanDoPaging;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPaging implements CanDoPaging {
    private int pageNumber = 0;
    private int pageSize = 10;
    private Sort.Direction sortDirection = Sort.Direction.ASC;
    private String sortBy = "id";

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
