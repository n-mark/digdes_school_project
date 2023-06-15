package ru.digdes.school.controller;

import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.digdes.school.dto.file.ProjectFilesDto;
import ru.digdes.school.dto.file.TaskFilesDto;
import ru.digdes.school.dto.task.ChangeTaskStateDto;
import ru.digdes.school.dto.task.TaskDto;
import ru.digdes.school.dto.task.TaskFilterObject;
import ru.digdes.school.dto.task.TaskPaging;
import ru.digdes.school.service.BasicService;
import ru.digdes.school.service.FileService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/tasks")
public class TaskController {
    private static final String INVOKER = "tasks";
    private final BasicService<TaskDto> taskService;
    private final FileService fileService;

    public TaskController(BasicService<TaskDto> taskService,
                          FileService fileService) {
        this.taskService = taskService;
        this.fileService = fileService;
    }

    @PostMapping
    public ResponseEntity<TaskDto> create(@Valid @RequestBody TaskDto taskDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(taskDto));
    }

    @PatchMapping
    public ResponseEntity<TaskDto> update(@Valid @RequestBody TaskDto taskDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.update(taskDto));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TaskDto>> search(TaskPaging taskPaging,
                                                TaskFilterObject taskFilterObject) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.search(taskPaging, taskFilterObject));
    }

    @PatchMapping("/status")
    public ResponseEntity<String> changeTaskState(@RequestBody ChangeTaskStateDto changeTaskStateDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.changeState(changeTaskStateDto));
    }

    @PostMapping("files/{taskId}")
    public ResponseEntity<TaskFilesDto> uploadFiles(@PathVariable Long taskId,
                                                    @RequestParam("files") MultipartFile[] files) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body((TaskFilesDto)fileService.upload(taskId, files, INVOKER));
    }

    @GetMapping("files/{taskId}/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable Long taskId,
                                            @PathVariable String filename) {
        Resource file = fileService.download(taskId, filename, INVOKER);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("files/{taskId}")
    public ResponseEntity<TaskFilesDto> getFilesList(@PathVariable Long taskId) {
        return ResponseEntity.ok((TaskFilesDto) fileService.getListOfFiles(taskId, INVOKER));
    }
}
