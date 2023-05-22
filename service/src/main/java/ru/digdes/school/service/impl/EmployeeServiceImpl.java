package ru.digdes.school.service.impl;

import ru.digdes.school.dao.facade.RepositoryFacade;
import ru.digdes.school.dao.jdbcimpl.EmployeeRepoFacadeJdbcImpl;
import ru.digdes.school.dto.employee.EmployeeDto;
import ru.digdes.school.dto.employeetask.EmployeeTaskDto;
import ru.digdes.school.mapping.impl.EmployeeMapperImpl;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.service.CommonService;

import java.util.List;

public class EmployeeServiceImpl implements CommonService<EmployeeDto> {
    private final RepositoryFacade<Employee> employeeRepository;
    private final Mapper<Employee, EmployeeDto> employeeDtoMapper;

    public EmployeeServiceImpl() {
//        this.employeeRepository = new RepoFacadeDataStorageImpl<Employee>(Employee.class);
        this.employeeRepository = new EmployeeRepoFacadeJdbcImpl();
        this.employeeDtoMapper = new EmployeeMapperImpl();
    }

    @Override
    public EmployeeDto create(EmployeeDto t) {
        Employee employee = employeeDtoMapper.dtoToModel(t);
        return employeeDtoMapper.modelToDto(
                employeeRepository.create(employee)
        );
    }

    @Override
    public EmployeeDto getOne(Long id) {
        return employeeDtoMapper.modelToDto(employeeRepository.getById(id));
    }

    @Override
    public List<EmployeeDto> getAll() {
        return employeeRepository.getAll().stream()
                .map(employeeDtoMapper::modelToDto)
                .toList();
    }

    @Override
    public EmployeeDto update(EmployeeDto t) {
        Employee employee = employeeDtoMapper.dtoToModel(t);
        return employeeDtoMapper.modelToDto(employeeRepository.update(employee));
    }

    @Override
    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }

    public List<EmployeeTaskDto> searchEmployeesAndTasks(String request) {
        return ((EmployeeRepoFacadeJdbcImpl) employeeRepository).search(request);
    }
}
