package ru.digdes.school.controller;

import ru.digdes.school.dto.EmployeeDto;
import ru.digdes.school.model.Employee;
import ru.digdes.school.security.SecurityConfig;
import ru.digdes.school.service.CommonService;
import ru.digdes.school.service.EmployeeService;

import java.util.logging.Logger;

public class EmployeeController {
    SecurityConfig securityConfig = new SecurityConfig();
    Logger logger = Logger.getLogger(EmployeeController.class.getName());

    public EmployeeDto getOne(Long id) {
        logger.info("EmployeeController.getOne() is in progress");
        CommonService<Employee, EmployeeDto> employeeService = new EmployeeService();
        return employeeService.getOne(id);
    }
}
