package ru.digdes.school.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.digdes.school.dao.repository.EmployeeRepository;
import ru.digdes.school.dao.repository.specifications.SpecificationFiltering;
import ru.digdes.school.dto.CanDoPaging;
import ru.digdes.school.dto.CanDoFiltering;
import ru.digdes.school.dto.Stateable;
import ru.digdes.school.dto.employee.DeleteEmployeeDto;
import ru.digdes.school.dto.employee.EmployeeDto;
import ru.digdes.school.dto.employee.EmployeeFilterObject;
import ru.digdes.school.dto.employee.SystemRolePatchDto;
import ru.digdes.school.exception.EmployeeDeletedException;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.EmployeeStatus;
import ru.digdes.school.model.project.Project;
import ru.digdes.school.service.BasicService;
import ru.digdes.school.service.GetService;
import ru.digdes.school.service.ProjectEmployeeRoleService;

import java.util.List;

@Service
public class EmployeeServiceImpl implements BasicService<EmployeeDto>,
        GetService<EmployeeDto> {
    private final EmployeeRepository employeeRepository;
    private final Mapper<Employee, EmployeeDto> mapper;
    private final SpecificationFiltering<Employee> specificationFiltering;
    private final ProjectEmployeeRoleService projectEmployeeRoleService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               Mapper<Employee, EmployeeDto> mapper,
                               SpecificationFiltering<Employee> specificationFiltering,
                               ProjectEmployeeRoleService projectEmployeeRoleService) {
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
        this.specificationFiltering = specificationFiltering;
        this.projectEmployeeRoleService = projectEmployeeRoleService;
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
            throw new EntityNotFoundException("The employee with id = " + updateFrom.getId() + " doesn't exist");
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
    public Page<EmployeeDto> search(CanDoPaging pagingObject, CanDoFiltering filteringObject) {
        EmployeeFilterObject employeeFilterObject = (EmployeeFilterObject) filteringObject;
        String[] keyWords = employeeFilterObject.getSearchString().orElse("").split(" ");

        Pageable pageable = PageRequest.of(pagingObject.getPageNumber(), pagingObject.getPageSize());

        Page<Employee> allFound = employeeRepository.findAll(
                Specification.where(specificationFiltering.withAllKeywords(keyWords))
                        .and(specificationFiltering.withFilters(filteringObject))
                        .and(specificationFiltering.setSorting(pagingObject.getSortDirection(), pagingObject.getSortBy())),
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

        List<Long> projectsIds = employee.getProjects().stream()
                .map(Project::getId)
                .toList();

        try {
            projectsIds.forEach(projectId ->
                    projectEmployeeRoleService.deleteTeamMember(projectId, deleteEmployeeDto.getId()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Can't set employee status to 'DELETED': " + e.getMessage());
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

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public SystemRolePatchDto changeSystemRole(SystemRolePatchDto systemRolePatchDto) {
        if (!employeeRepository.existsById(systemRolePatchDto.getId())) {
            throw new EntityNotFoundException("An employee with id = " + systemRolePatchDto.getId() + " doesn't exist");
        }
        Employee employee = employeeRepository.getReferenceById(systemRolePatchDto.getId());
        employee.setRoleInSystem(systemRolePatchDto.getRoleInSystem());
        return new SystemRolePatchDto(employeeRepository.save(employee));
    }
}
