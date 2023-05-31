package ru.digdes.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final BasicService<EmployeeDto> employeeService;

    public EmployeeController(BasicService<EmployeeDto> employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @Operation(description = "Создать сотрудника")
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.create(employeeDto));
    }

    @GetMapping("/search")
    @Operation(description = "Найти сотрудников")
    public ResponseEntity<Page<EmployeeDto>> search(EmployeePaging employeePaging,
                                                    EmployeeFilterObject employeeFilterObject) {
        return ResponseEntity.ok(employeeService.search(employeePaging, employeeFilterObject));
    }


    @GetMapping("/id/{id}")
    @Operation(description = "Получить сотрудника по идентификатору")
    public ResponseEntity<EmployeeDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(((GetService<EmployeeDto>) employeeService).getOneById(id));
    }


    @GetMapping("/account/{account}")
    @Operation(description = "Получить сотрудника по УЗ")
    public ResponseEntity<EmployeeDto> getByAccount(@PathVariable String account) {
        return ResponseEntity.ok(((GetService<EmployeeDto>) employeeService).getOneByStringValue(account));
    }

    @PatchMapping
    @Operation(description = "Изменить данные сотрудника")
    public ResponseEntity<EmployeeDto> updateEmployee(@RequestBody EmployeeDto employeeDto) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(employeeService.update(employeeDto));
    }

    @DeleteMapping
    @Operation(description = "Перевести сотрудника в статус 'Удалён'")
    public ResponseEntity<String> deleteEmployee(DeleteEmployeeDto deleteEmployeeDto) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(employeeService.changeState(deleteEmployeeDto));
    }
}
