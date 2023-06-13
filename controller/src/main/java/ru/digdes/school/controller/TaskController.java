package ru.digdes.school.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.digdes.school.dto.task.ChangeTaskStateDto;
import ru.digdes.school.dto.task.TaskDto;
import ru.digdes.school.dto.task.TaskFilterObject;
import ru.digdes.school.dto.task.TaskPaging;
import ru.digdes.school.service.BasicService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final BasicService<TaskDto> taskService;

    public TaskController(BasicService<TaskDto> taskService) {
        this.taskService = taskService;
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
}
