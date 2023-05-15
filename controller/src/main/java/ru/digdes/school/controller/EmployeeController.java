package ru.digdes.school.controller;

import ru.digdes.school.dto.employee.EmployeeDto;
import ru.digdes.school.service.CommonService;
import ru.digdes.school.service.impl.EmployeeServiceImpl;

import java.util.List;
import java.util.logging.Logger;

public class EmployeeController {
    CommonService<EmployeeDto> employeeService = new EmployeeServiceImpl();

    public void create(EmployeeDto employeeDto) {
        employeeService.create(employeeDto);
    }

    public void update(EmployeeDto employeeDto) {
        employeeService.update(employeeDto);
    }

    public List<EmployeeDto> getAll() {
        return employeeService.getAll();
    }

    public EmployeeDto getOne(Long id) {
        return employeeService.getOne(id);
    }

    public void delete(Long id) {
        employeeService.delete(id);
    }
}
