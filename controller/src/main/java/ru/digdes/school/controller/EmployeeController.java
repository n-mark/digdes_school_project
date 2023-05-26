package ru.digdes.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.digdes.school.dto.employee.EmployeeDto;
import ru.digdes.school.service.impl.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employeeDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(employeeDto));
    }

    @GetMapping(params = {"search"})
    public ResponseEntity<List<EmployeeDto>> search(@RequestParam(value = "search") String search) {
        return ResponseEntity.ok(employeeService.searchEmployees(search));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAll() {
        return ResponseEntity.ok(employeeService.getAll());
    }

    @GetMapping(params = {"id"})
    public ResponseEntity<?> getById(@RequestParam(value = "id") Long id) {
        try {
            return ResponseEntity.ok(employeeService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(params = {"account"})
    public ResponseEntity<?> getByAccount(@RequestParam(value = "account") String account) {
        try {
            return ResponseEntity.ok(employeeService.getByAccount(account));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping
    public ResponseEntity<?> updateEmployee(@RequestBody EmployeeDto employeeDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(employeeService.updateEmployee(employeeDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping(params = {"id"})
    public ResponseEntity<?> deleteEmployee(@RequestParam(value = "id") Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("The employee status with the id = " + id + " has been set to 'DELETED'");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
