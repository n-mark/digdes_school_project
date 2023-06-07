package ru.digdes.school.dao.repository.specifications;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.digdes.school.dto.CanFilter;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.EmployeeStatus;

import java.util.ArrayList;
import java.util.List;

@Component
public class EmployeeSpecificationImpl implements SpecificationFiltering<Employee>{
    @Override
    public Specification<Employee> withFilters(CanFilter filteringObject) {
        return (employee, cq, cb) ->
                cb.equal(employee.get("status"), EmployeeStatus.ACTIVE);
    }

    @Override
    public Specification<Employee> withAllKeywords(String[] keywords) {
        return (employee, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (String keyword : keywords) {
                predicates.add(cb.or(
                        cb.like(cb.lower(employee.get("lastName")), "%" + keyword.toLowerCase() + "%"),
                        cb.like(cb.lower(employee.get("name")), "%" + keyword.toLowerCase() + "%"),
                        cb.like(cb.lower(employee.get("middleName")), "%" + keyword.toLowerCase() + "%"),
                        cb.like(cb.lower(employee.get("account")), "%" + keyword.toLowerCase() + "%"),
                        cb.like(cb.lower(employee.get("email")), "%" + keyword.toLowerCase() + "%")
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public Specification<Employee> setSorting(Sort.Direction sortDirection, String sortBy) {
        return (employee, cq, cb) -> {
            if (sortDirection.isAscending()) {
                cq.orderBy(cb.asc(employee.get(sortBy)));
            } else {
                cq.orderBy(cb.desc(employee.get(sortBy)));
            }
            return cb.conjunction();
        };
    }
}
