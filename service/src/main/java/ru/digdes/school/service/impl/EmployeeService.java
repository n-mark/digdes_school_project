package ru.digdes.school.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import ru.digdes.school.dao.repository.EmployeeRepository;
import ru.digdes.school.dto.employee.EmployeeDto;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.EmployeeStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final Mapper<Employee, EmployeeDto> mapper;

    public EmployeeService(EmployeeRepository employeeRepository,
                           Mapper<Employee, EmployeeDto> mapper) {
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
    }

    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        Employee employee = mapper.dtoToModel(employeeDto);
        employee.setStatus(EmployeeStatus.ACTIVE);
        return mapper.modelToDto(employeeRepository.save(employee));
    }

    public EmployeeDto getById(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("An employee with id = " + id + " doesn't exist");
        }
        Employee employee = employeeRepository.getReferenceById(id);
        return mapper.modelToDto(employee);
    }

    public EmployeeDto getByAccount(String account) {
        if (!employeeRepository.existsByAccount(account)) {
            throw new EntityNotFoundException("An employee with account '" + account + "' doesn't exist");
        }
        Employee employee = employeeRepository.getEmployeeByAccount(account);
        return mapper.modelToDto(employee);
    }

    public List<EmployeeDto> searchEmployees(String search) {
        String[] keyWords = search.split(" ");
        List<Employee> allFound = employeeRepository.foundByString(keyWords[0]);
        if (keyWords.length > 1) {
            for (int i = 1; i < keyWords.length; i++) {
                allFound.retainAll(employeeRepository.foundByString(keyWords[i]));
            }
        }
        return allFound.stream()
                .map(mapper::modelToDto)
                .collect(Collectors.toList());
    }

    public List<EmployeeDto> getAll() {
        return employeeRepository.findAll().stream()
                .map(mapper::modelToDto)
                .collect(Collectors.toList());
    }

    public EmployeeDto updateEmployee(EmployeeDto employeeDto) throws IllegalAccessException {
        if (!employeeRepository.existsById(employeeDto.getId())) {
            throw new EntityNotFoundException("An employee with id = " + employeeDto.getId() + " doesn't exist");
        }

        Employee employee = employeeRepository.getReferenceById(employeeDto.getId());
        if (employee.getStatus().equals(EmployeeStatus.DELETED)) {
            throw new IllegalAccessException
                    ("The employee with id = " + employee.getId() + " status is 'DELETED'. Forbidden to update.");
        }

        mapper.updateMerge(employee, employeeDto);
        return mapper.modelToDto(employeeRepository.save(employee));
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("An employee with id = " + id + " doesn't exist");
        }
        Employee employee = employeeRepository.getReferenceById(id);
        employee.setStatus(EmployeeStatus.DELETED);
        employeeRepository.save(employee);
    }
}
