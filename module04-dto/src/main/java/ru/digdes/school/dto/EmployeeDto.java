package ru.digdes.school.dto;

import ru.digdes.school.model.Employee;

import java.util.logging.Logger;

public class EmployeeDto {
    Logger logger = Logger.getLogger(EmployeeDto.class.getName());
    public EmployeeDto(Employee e) {
        logger.info("EmployeeDto constructor has been called");
    }
}
