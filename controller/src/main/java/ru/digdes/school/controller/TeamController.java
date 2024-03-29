package ru.digdes.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.digdes.school.dto.team.ProjectEmployeeRoleDto;
import ru.digdes.school.dto.team.TeamDto;
import ru.digdes.school.service.ProjectEmployeeRoleService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/teams")
@Tag(name = "Команды")
public class TeamController {
    private final ProjectEmployeeRoleService projectEmployeeRoleService;

    public TeamController(ProjectEmployeeRoleService projectEmployeeRoleService) {
        this.projectEmployeeRoleService = projectEmployeeRoleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Добавить сотрудника на проект",
            description = "Добавить сотрудника на проект. Требуются права администратора")
    public ResponseEntity<ProjectEmployeeRoleDto> addEmployeeToProject(
                                    @RequestBody ProjectEmployeeRoleDto projectEmployeeRoleDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectEmployeeRoleService.addTeamMember(projectEmployeeRoleDto));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить сотрудника с проекта",
            description = "Сотрудник может быть удален с проекта если все его задачи " +
                    "находятся в статусе 'Закрыта'. Требуются права администратора")
    public ResponseEntity<String> removeEmployeeFromProject(@RequestParam Long projectId,
                                                            @RequestParam Long employeeId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(projectEmployeeRoleService.deleteTeamMember(projectId, employeeId));
    }

    @GetMapping()
    @Operation(summary = "Получить команду проекта")
    public ResponseEntity<TeamDto> getProjectTeam(Long projectId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(projectEmployeeRoleService.getProjectTeam(projectId));
    }
}
