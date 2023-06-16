package ru.digdes.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Сотрудники")
public class EmployeeController {
    private final BasicService<EmployeeDto> employeeService;

    public EmployeeController(BasicService<EmployeeDto> employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(summary = "Создать профиль сотрудника",
            description = "Поле \"id\" генерируется автоматически на стороне базы данных и будет проигнорировано. " +
                    "Требуются права администратора")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.create(employeeDto));
    }

    @Operation(summary = "Поиск сотрудников",
            description = "Найти сотрудников. Поиск будет осуществлен с конъюнкцией по всем переданным" +
                    " в строке значениям. Каждое значение будет проверяться на частичное " +
                    "соответствие ('%значение%') без учета регистра. Также можно использовать данный " +
                    "эндпоинт для получения всех сотрудников, если передать пустую строку запроса. " +
                    "Отображаются только сотрудники со статусом \"Активный\". По умолчанию список отсортирован " +
                    "по фамилии ('lastName') в порядке возрастания."
    )
    @GetMapping("/search")
    public ResponseEntity<Page<EmployeeDto>> search(@ParameterObject EmployeePaging employeePaging,
                                                    @ParameterObject EmployeeFilterObject employeeFilterObject) {
        return ResponseEntity.ok(employeeService.search(employeePaging, employeeFilterObject));
    }


    @Operation(summary = "Получить сотрудника по идентификатору")
    @GetMapping("/id/{id}")
    public ResponseEntity<EmployeeDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(((GetService<EmployeeDto>) employeeService).getOneById(id));
    }


    @Operation(summary = "Получить сотрудника по УЗ")
    @GetMapping("/account/{account}")
    public ResponseEntity<EmployeeDto> getByAccount(@PathVariable String account) {
        return ResponseEntity.ok(((GetService<EmployeeDto>) employeeService).getOneByStringValue(account));
    }

    @Operation(summary = "Измененить данные сотрудника",
            description = "Требуются права администратора")
    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDto> updateEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(employeeService.update(employeeDto));
    }

    @Operation(summary = "Удалить сотрудника",
            description = "Перевести сотрудника в статус 'Удалён'. При этом сотрудник автоматически удаляется " +
                    "с назначенных проектов, при условии, что все назначенные на него задачи находятся в статусе " +
                    "'Закрыта'. Требуются права администратора.")
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEmployee(DeleteEmployeeDto deleteEmployeeDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(employeeService.changeState(deleteEmployeeDto));
    }

    @Operation(summary = "Изменить роль сотрудника в системе",
            description = "Изменить роль сотрудника в системе. Доступна только администраторам")
    @PatchMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemRolePatchDto> chahgeSystemRole(@RequestBody SystemRolePatchDto systemRolePatchDto) {
        return ResponseEntity.ok(((EmployeeServiceImpl) employeeService)
                .changeSystemRole(systemRolePatchDto));
    }
}
