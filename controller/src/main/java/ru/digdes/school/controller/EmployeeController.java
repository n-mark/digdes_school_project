package ru.digdes.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.digdes.school.dto.employee.*;
import ru.digdes.school.service.BasicService;
import ru.digdes.school.service.GetService;
import ru.digdes.school.service.impl.EmployeeServiceImpl;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final BasicService<EmployeeDto> employeeService;

    public EmployeeController(BasicService<EmployeeDto> employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(description = "Создать сотрудника")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.create(employeeDto));
    }

    @Operation(description = "Найти сотрудников")
    @GetMapping("/search")
    public ResponseEntity<Page<EmployeeDto>> search(EmployeePaging employeePaging,
                                                    EmployeeFilterObject employeeFilterObject) {
        return ResponseEntity.ok(employeeService.search(employeePaging, employeeFilterObject));
    }


    @Operation(description = "Получить сотрудника по идентификатору")
    @GetMapping("/id/{id}")
    public ResponseEntity<EmployeeDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(((GetService<EmployeeDto>) employeeService).getOneById(id));
    }


    @Operation(description = "Получить сотрудника по УЗ")
    @GetMapping("/account/{account}")
    public ResponseEntity<EmployeeDto> getByAccount(@PathVariable String account) {
        return ResponseEntity.ok(((GetService<EmployeeDto>) employeeService).getOneByStringValue(account));
    }

    @Operation(description = "Изменить данные сотрудника")
    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDto> updateEmployee(@RequestBody EmployeeDto employeeDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(employeeService.update(employeeDto));
    }

    @Operation(description = "Перевести сотрудника в статус 'Удалён'")
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEmployee(DeleteEmployeeDto deleteEmployeeDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(employeeService.changeState(deleteEmployeeDto));
    }

    @Operation(description = "Изменить роль сотрудника в системе. Доступна только администраторам")
    @PatchMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemRolePatchDto> chahgeSystemRole(@RequestBody SystemRolePatchDto systemRolePatchDto) {
        return ResponseEntity.ok(((EmployeeServiceImpl) employeeService)
                .changeSystemRole(systemRolePatchDto));
    }
}
