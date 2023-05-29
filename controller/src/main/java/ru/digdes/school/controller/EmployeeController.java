package ru.digdes.school.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.digdes.school.dto.employee.DeleteEmployeeDto;
import ru.digdes.school.dto.employee.EmployeeDto;
import ru.digdes.school.dto.employee.EmployeeFilterObject;
import ru.digdes.school.dto.employee.EmployeePaging;
import ru.digdes.school.service.BasicService;
import ru.digdes.school.service.GetService;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final BasicService<EmployeeDto> employeeService;

    public EmployeeController(BasicService<EmployeeDto> employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employeeDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.create(employeeDto));
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeDto>> search(EmployeePaging employeePaging,
                                                    EmployeeFilterObject employeeFilterObject) {
        return ResponseEntity.ok(employeeService.search(employeePaging, employeeFilterObject));
    }

    @GetMapping(params = {"id"})
    public ResponseEntity<EmployeeDto> getById(@RequestParam(value = "id") Long id) {
        return ResponseEntity.ok(((GetService<EmployeeDto>) employeeService).getOneById(id));
    }

    @GetMapping(params = {"account"})
    public ResponseEntity<EmployeeDto> getByAccount(@RequestParam(value = "account") String account) {
        return ResponseEntity.ok(((GetService<EmployeeDto>) employeeService).getOneByStringValue(account));
    }

    @PatchMapping
    public ResponseEntity<?> updateEmployee(@RequestBody EmployeeDto employeeDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(employeeService.update(employeeDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteEmployee(DeleteEmployeeDto deleteEmployeeDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(employeeService.changeState(deleteEmployeeDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
