package ru.digdes.school.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.digdes.school.dto.project.ChangeProjectStateDto;
import ru.digdes.school.dto.project.ProjectDto;
import ru.digdes.school.dto.project.ProjectFilterObject;
import ru.digdes.school.dto.project.ProjectPaging;
import ru.digdes.school.service.BasicService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final BasicService<ProjectDto> projectService;

    public ProjectController(BasicService<ProjectDto> projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.create(projectDto));
    }

    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectDto> updateProject(@RequestBody ProjectDto projectDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(projectService.update(projectDto));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProjectDto>> search(ProjectPaging projectPaging,
                                                   ProjectFilterObject projectFilterObject) {
        return ResponseEntity.ok(projectService.search(projectPaging, projectFilterObject));
    }

    @PatchMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeProjectState(@RequestBody ChangeProjectStateDto changeProjectStateDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(projectService.changeState(changeProjectStateDto));
    }
}
