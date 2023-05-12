package ru.digdes.school.service;

import ru.digdes.school.dao.EmployeeRepository;
import ru.digdes.school.dao.impl.EmployeeRepositoryImpl;
import ru.digdes.school.dto.EmployeeDto;
import ru.digdes.school.model.Employee;

import java.util.List;
import java.util.logging.Logger;

public class EmployeeService implements CommonService<Employee, EmployeeDto> {
    Logger logger = Logger.getLogger(EmployeeService.class.getName());

    @Override
    public EmployeeDto getOne(Long id) {
        logger.info("EmployeeService.getOne() is performing");
        EmployeeRepository<Employee, Long> employeeRepository = new EmployeeRepositoryImpl();
        return new EmployeeDto(employeeRepository.getOne(id));
    }

    @Override
    public List<EmployeeDto> getAll() {
        return null;
    }
}
