package ru.digdes.school.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.digdes.school.dao.repository.EmployeeRepository;
import ru.digdes.school.dto.CanBePaged;
import ru.digdes.school.dto.CanFilter;
import ru.digdes.school.dto.Stateable;
import ru.digdes.school.dto.employee.DeleteEmployeeDto;
import ru.digdes.school.dto.employee.EmployeeDto;
import ru.digdes.school.dto.employee.EmployeeFilterObject;
import ru.digdes.school.exception.EmployeeDeletedException;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.EmployeeStatus;
import ru.digdes.school.service.BasicService;
import ru.digdes.school.service.GetService;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements BasicService<EmployeeDto>,
                                            GetService<EmployeeDto> {
    private final EmployeeRepository employeeRepository;
    private final Mapper<Employee, EmployeeDto> mapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               Mapper<Employee, EmployeeDto> mapper) {
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public EmployeeDto create(EmployeeDto createFrom) {
        Employee employee = mapper.dtoToModel(createFrom);
        employee.setStatus(EmployeeStatus.ACTIVE);
        return mapper.modelToDto(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public EmployeeDto update(EmployeeDto updateFrom) {
        if (!employeeRepository.existsById(updateFrom.getId())) {
            throw new EntityNotFoundException("An employee with id = " + updateFrom.getId() + " doesn't exist");
        }

        Employee employee = employeeRepository.getReferenceById(updateFrom.getId());
        if (employee.getStatus().equals(EmployeeStatus.DELETED)) {
            throw new IllegalArgumentException
                    ("The employee with id = " + employee.getId() + " status is 'DELETED'. Forbidden to update.");
        }

        mapper.updateMerge(employee, updateFrom);
        return mapper.modelToDto(employeeRepository.save(employee));
    }

    @Override
    public Page<EmployeeDto> search(CanBePaged pagingObject, CanFilter filteringObject) {
        EmployeeFilterObject employeeFilterObject = (EmployeeFilterObject) filteringObject;
        String[] keyWords = employeeFilterObject.getSearchString().orElse("").split(" ");

        Pageable pageable = PageRequest.of(pagingObject.getPageNumber(), pagingObject.getPageSize());

        Page<Employee> allFound = employeeRepository.findAll(
                Specification.where(byAllKeywords(keyWords))
                        .and(isStatusActive())
                        .and(setSorting(pagingObject.getSortDirection(), pagingObject.getSortBy())),
                pageable);

        return allFound.map(mapper::modelToDto);
    }

    @Override
    @Transactional
    public String changeState(Stateable changeStateObject) {
        DeleteEmployeeDto deleteEmployeeDto = (DeleteEmployeeDto) changeStateObject;
        if (!employeeRepository.existsById(deleteEmployeeDto.getId())) {
            throw new EntityNotFoundException("An employee with id = " + deleteEmployeeDto.getId() + " doesn't exist");
        }
        Employee employee = employeeRepository.getReferenceById(deleteEmployeeDto.getId());
        if (employee.getStatus().equals(EmployeeStatus.DELETED)) {
            throw new EmployeeDeletedException("The employee with id = " + deleteEmployeeDto.getId()
                    + " status is already 'DELETED'");
        }
        employee.setStatus(EmployeeStatus.DELETED);
        employeeRepository.save(employee);
        return "The employee with id = " + deleteEmployeeDto.getId() + " status has been set to 'DELETED'";
    }

    @Override
    public EmployeeDto getOneById(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("An employee with id = " + id + " doesn't exist");
        }
        Employee employee = employeeRepository.getReferenceById(id);
        if (employee.getStatus().equals(EmployeeStatus.DELETED)) {
            throw new EmployeeDeletedException("The employee " + mapper.modelToDto(employee) +
                    " status is 'DELETED'");
        }
        return mapper.modelToDto(employee);
    }

    @Override
    public EmployeeDto getOneByStringValue(String value) {
        if (!employeeRepository.existsByAccount(value)) {
            throw new EntityNotFoundException("An employee with account '" + value + "' doesn't exist");
        }
        Employee employee = employeeRepository.getEmployeeByAccount(value);
        if (employee.getStatus().equals(EmployeeStatus.DELETED)) {
            throw new EmployeeDeletedException("The employee " + mapper.modelToDto(employee) +
                    " status is 'DELETED'");
        }
        return mapper.modelToDto(employee);
    }



    private Specification<Employee> isStatusActive() {
        return (employee, cq, cb) ->
                cb.equal(employee.get("status"), EmployeeStatus.ACTIVE);
    }


    private Specification<Employee> byAllKeywords(String[] keywords) {
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

    private Specification<Employee> setSorting(Sort.Direction sortDirection, String sortBy) {
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
