package ru.digdes.school.dao.repository.specifications;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.digdes.school.dto.CanDoFiltering;
import ru.digdes.school.dto.project.ProjectFilterObject;
import ru.digdes.school.model.project.Project;
import ru.digdes.school.model.project.ProjectStatus;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProjectSpecificationImpl implements SpecificationFiltering<Project> {
    @Override
    public Specification<Project> withFilters(CanDoFiltering filteringObject) {
        return (project, cq, cb) -> {
            ProjectFilterObject projectFilterObject = (ProjectFilterObject) filteringObject;
            if (projectFilterObject.getStatus().isEmpty()) {
                return cb.isTrue(cb.literal(true));
            }

            List<Predicate> predicates = new ArrayList<>();

            for(ProjectStatus projectStatus : projectFilterObject.getStatus()) {
                predicates.add(cb.equal(project.get("projectStatus"), projectStatus));
            }

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }


    @Override
    public Specification<Project> withAllKeywords(String[] keywords) {
        return (project, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (String keyword : keywords) {
                predicates.add(cb.or(
                        cb.like(cb.lower(project.get("projectCode")), "%" + keyword.toLowerCase() + "%"),
                        cb.like(cb.lower(project.get("projectName")), "%" + keyword.toLowerCase() + "%")
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public Specification<Project> setSorting(Sort.Direction sortDirection, String sortBy) {
        return (project, cq, cb) -> {
            if (sortDirection.isAscending()) {
                cq.orderBy(cb.asc(project.get(sortBy)));
            } else {
                cq.orderBy(cb.desc(project.get(sortBy)));
            }
            return cb.conjunction();
        };
    }
}
