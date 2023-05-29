package ru.digdes.school.dto;

import org.springframework.data.domain.Sort;

public interface CanBePaged {
    int getPageNumber();
    int getPageSize();
    Sort.Direction getSortDirection();
    String getSortBy();
}
