package ru.digdes.school.dao.repository.specifications;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.digdes.school.dto.CanFilter;

public interface SpecificationFiltering<T> {
    Specification<T> withFilters(CanFilter filteringObject);
    Specification<T> withAllKeywords(String[] keywords);
    Specification<T> setSorting(Sort.Direction sortDirection, String sortBy);
}
