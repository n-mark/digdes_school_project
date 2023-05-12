package ru.digdes.school.app;

import ru.digdes.school.controller.EmployeeController;

public class Launch {
    public static void main(String[] args) {
        EmployeeController employeeController = new EmployeeController();
        employeeController.getOne(1L);
        System.out.println("This is the future project layout. Nothing to perform so far. Have a nice day! Bye!");
    }
}
