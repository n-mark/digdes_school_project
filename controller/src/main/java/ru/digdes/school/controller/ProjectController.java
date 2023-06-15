package ru.digdes.school.controller;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.digdes.school.dto.file.ProjectFilesDto;
import ru.digdes.school.dto.project.ChangeProjectStateDto;
import ru.digdes.school.dto.project.ProjectDto;
import ru.digdes.school.dto.project.ProjectFilterObject;
import ru.digdes.school.dto.project.ProjectPaging;
import ru.digdes.school.service.BasicService;
import ru.digdes.school.service.FileService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final BasicService<ProjectDto> projectService;
    private final FileService fileService;
    private final String INVOKER = "projects";


    public ProjectController(BasicService<ProjectDto> projectService,
                             FileService fileService) {
        this.projectService = projectService;
        this.fileService = fileService;
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

    @PostMapping("files/{projectId}")
    public ResponseEntity<ProjectFilesDto> uploadFiles(@PathVariable Long projectId,
                                                       @RequestParam("files") MultipartFile[] files) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body((ProjectFilesDto)fileService.upload(projectId, files, INVOKER));
    }

    @GetMapping("files/{projectId}/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable Long projectId,
                                            @PathVariable String filename) {
        Resource file = fileService.download(projectId, filename, INVOKER);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("files/{projectId}")
    public ResponseEntity<ProjectFilesDto> getFilesList(@PathVariable Long projectId) {
        return ResponseEntity.ok((ProjectFilesDto) fileService.getListOfFiles(projectId, INVOKER));
    }
}
