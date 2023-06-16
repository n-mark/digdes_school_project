package ru.digdes.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Проекты")
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
    @Operation(summary = "Создать проект",
            description = "Создать проект. Требуются права администратора")
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.create(projectDto));
    }

    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Изменение проекта",
            description = "Изменение полей проекта. Требуются права администратора")
    public ResponseEntity<ProjectDto> updateProject(@RequestBody ProjectDto projectDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(projectService.update(projectDto));
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск проектов",
            description = "Поиск по текстовому значению с коньюнкцией (может быть передано несколько значений). " +
                    "Может быть выбрано несколько фильтров по статусу проекта. Если передана пустая строка - " +
                    "и отсутствуют фильтры - возвращаются все имеющиеся записи. Также доступно постраничное" +
                    "отображение и сортировка")
    public ResponseEntity<Page<ProjectDto>> search(@ParameterObject ProjectPaging projectPaging,
                                                   @ParameterObject ProjectFilterObject projectFilterObject) {
        return ResponseEntity.ok(projectService.search(projectPaging, projectFilterObject));
    }

    @PatchMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Перевод проекта в другое состояние",
            description = "Изменить состояние проекта. Переход возможен по согласно следованию статусов" +
                    "слева направо: Черновик -> В разработке -> В тестировании -> Завершен")
    public ResponseEntity<String> changeProjectState(@RequestBody ChangeProjectStateDto changeProjectStateDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(projectService.changeState(changeProjectStateDto));
    }

    @PostMapping("files/{projectId}")
    @Operation(summary = "Добавить файлы к проекту",
            description = "Добавляет файлы к проекту с переданным id. Можно выбрать несколько файлов для загрузки." +
                    "Максимальный размер одного файла 200 МБ")
    public ResponseEntity<ProjectFilesDto> uploadFiles(@PathVariable Long projectId,
                                                       @RequestParam("files") MultipartFile[] files) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body((ProjectFilesDto) fileService.upload(projectId, files, INVOKER));
    }

    @GetMapping("files/{projectId}/{filename:.+}")
    @Operation(summary = "Получить файл по ссылке", description = "После получения списка файлов вы можете перейти" +
            "по ссылке, после чего начнется загрузка файла. Данный эндпоинт и служит для этой цели")
    public ResponseEntity<Resource> getFile(@PathVariable Long projectId,
                                            @PathVariable String filename) {
        Resource file = fileService.download(projectId, filename, INVOKER);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("files/{projectId}")
    @Operation(summary = "Получить список всех файлов на проекте",
            description = "Принимает id проекта и возвращает список файлов для данного проекта")
    public ResponseEntity<ProjectFilesDto> getFilesList(@PathVariable Long projectId) {
        return ResponseEntity.ok((ProjectFilesDto) fileService.getListOfFiles(projectId, INVOKER));
    }
}
