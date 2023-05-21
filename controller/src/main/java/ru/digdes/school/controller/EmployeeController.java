package ru.digdes.school.controller;

import ru.digdes.school.dto.employee.EmployeeDto;
import ru.digdes.school.service.CommonService;
import ru.digdes.school.service.impl.EmployeeServiceImpl;

import java.util.List;
import java.util.logging.Logger;

public class EmployeeController {
    CommonService<EmployeeDto> employeeService = new EmployeeServiceImpl();

    public void create(EmployeeDto employeeDto) {
        System.out.println("Created");
        display(employeeService.create(employeeDto));
    }

    public void update(EmployeeDto employeeDto) {
        System.out.println("Updated");
        display(employeeService.update(employeeDto));
    }

    public void getAll() {
        System.out.println("Retrieved all");
        employeeService.getAll().forEach(System.out::println);
    }

    public void getOne(Long id) {
        System.out.println("Retrieved one");
        display(employeeService.getOne(id));
    }

    public void delete(Long id) {
        System.out.println("Deleted");
        employeeService.delete(id);
    }

    public void searchEmployeesAndTasks(String request) {
        System.out.println("Found");
        ((EmployeeServiceImpl) employeeService).searchEmployeesAndTasks(request)
                .forEach(System.out::println);
    }

    private void display(Object o) {
        System.out.println(o);
    }
}
