package ru.digdes.school.dao.impl;

import ru.digdes.school.dao.EmployeeRepository;
import ru.digdes.school.model.Employee;

import java.util.logging.Logger;

public class EmployeeRepositoryImpl implements EmployeeRepository<Employee, Long> {
    Logger logger = Logger.getLogger(EmployeeRepositoryImpl.class.getName());
    @Override
    public Employee getOne(Long aLong) {
        logger.info("EmployeeRepositoryImpl.getOne() is in progress");
        return null;
    }
}
