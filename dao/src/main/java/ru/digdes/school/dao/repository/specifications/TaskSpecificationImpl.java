package ru.digdes.school.dao.repository.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.digdes.school.dto.CanDoFiltering;
import ru.digdes.school.dto.task.TaskFilterObject;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.task.Task;
import ru.digdes.school.model.task.TaskStatus;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskSpecificationImpl implements SpecificationFiltering<Task>{
    @Override
    public Specification<Task> withFilters(CanDoFiltering filteringObject) {
        return (task, cq, cb) -> {
            TaskFilterObject taskFilteringObject = (TaskFilterObject) filteringObject;
            List<Predicate> predicates = new ArrayList<>();

            if (!taskFilteringObject.getStatus().isEmpty()) {
                predicates.add(task.get("taskStatus").in(taskFilteringObject.getStatus()));
            }

            if (!taskFilteringObject.getResponsible().isEmpty()) {
                Join<Task, Employee> responsibleJoin = task.join("responsible");
                predicates.add(responsibleJoin.get("id").in(taskFilteringObject.getResponsible()));
            }

            if (!taskFilteringObject.getCreatedBy().isEmpty()) {
                Join<Task, Employee> createdByJoin = task.join("createdBy");
                predicates.add(createdByJoin.get("id").in(taskFilteringObject.getCreatedBy()));
            }

            if (taskFilteringObject.getDeadlineFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(task.get("deadline"), taskFilteringObject.getDeadlineFrom()));
            }

            if (taskFilteringObject.getDeadlineTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(task.get("deadline"), taskFilteringObject.getDeadlineTo()));
            }

            if (taskFilteringObject.getCreatedWhenFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(task.get("createdWhen"), taskFilteringObject.getCreatedWhenFrom()));
            }

            if (taskFilteringObject.getCreatedWhenTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(task.get("createdWhen"), taskFilteringObject.getCreatedWhenTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }



    @Override
    public Specification<Task> withAllKeywords(String[] keywords) {
        return (task, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (String keyword : keywords) {
                predicates.add(cb.or(
                        cb.like(cb.lower(task.get("taskName")), "%" + keyword.toLowerCase() + "%")
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public Specification<Task> setSorting(Sort.Direction sortDirection, String sortBy) {
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
