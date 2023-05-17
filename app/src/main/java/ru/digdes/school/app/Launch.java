package ru.digdes.school.app;

import ru.digdes.school.controller.EmployeeController;
import ru.digdes.school.dto.employee.EmployeeDto;

public class Launch {
    public static void main(String[] args) {
        EmployeeController employeeController = new EmployeeController();
        EmployeeDto employeeDto = EmployeeDto.builder()
                .name("John")
                .lastName("Travolta")
                .build();
        employeeController.create(employeeDto);

        EmployeeDto employeeDto2 = EmployeeDto.builder()
                .name("Dwayne")
                .lastName("Johnson")
                .build();
        employeeController.create(employeeDto2);

        EmployeeDto employeeDto3 = EmployeeDto.builder()
                .name("Bruce")
                .lastName("Willis")
                .build();
        employeeController.create(employeeDto3);

        EmployeeDto employeeDto5 = EmployeeDto.builder()
                .id(2L)
                .name("Дуйн")
                .lastName("Johnson")
                .position("CHIEF")
                .jobTitle("ACCOUNTANT")
                .build();
        employeeController.update(employeeDto5);


        EmployeeDto employeeDto4 = EmployeeDto.builder()
                .id(3L)
                .name("Брюс")
                .build();
        employeeController.update(employeeDto4);

        employeeController.getAll();

        employeeController.delete(3L);

        employeeController.getAll();
    }
}
