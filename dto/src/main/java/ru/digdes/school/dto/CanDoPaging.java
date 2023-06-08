package ru.digdes.school.dto;

import org.springframework.data.domain.Sort;

public interface CanDoPaging {
    int getPageNumber();
    int getPageSize();
    Sort.Direction getSortDirection();
    String getSortBy();
}
