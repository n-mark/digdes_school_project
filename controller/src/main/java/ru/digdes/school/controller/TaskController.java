package ru.digdes.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.digdes.school.dto.file.TaskFilesDto;
import ru.digdes.school.dto.task.*;
import ru.digdes.school.service.BasicService;
import ru.digdes.school.service.FileService;
import ru.digdes.school.service.impl.TaskServiceImpl;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/tasks")
@Tag(name = "Задачи")
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
    @Operation(summary = "Создать задачу",
            description = "Создание задачи. При создании задачи требуется указывать, к какому проекту она принадлежит." +
                    " Если аутентифицированный пользователь, создающий задачу, не является участником данного проекта," +
                    "будет выброшено исключение. Если при создании указывается ответственный сотрудник, он также " +
                    "должен принадлежать указанному проекту. При наличии email у сотрудника ему будет отправлено" +
                    " сообщение на почтовый ящик с уведомлением о назначении задачи. Если вы хотите протестировать" +
                    " отправку почты, передайте 'testEmail' в теле TaskDto.")
    public ResponseEntity<TaskDto> create(@Valid @RequestBody TaskDto taskDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(taskDto));
    }

    @PatchMapping
    @Operation(summary = "Изменить задачу",
            description = "Если аутентифицированный пользователь, пытающийся изменить задачу, не является " +
                    "участником данного проекта, будет выброшено исключение. Если при изменении указывается " +
                    "ответственный сотрудник, он также должен принадлежать указанному проекту. При наличии email " +
                    "у сотрудника ему будет отправлено сообщение на почтовый ящик с уведомлением о назначении задачи. " +
                    "Если вы хотите протестировать отправку почты, передайте 'testEmail' в теле TaskDto.")
    public ResponseEntity<TaskDto> update(@Valid @RequestBody TaskDto taskDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.update(taskDto));
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск задач",
            description = "Поиск по текстовому значению с коньюнкцией (может быть передано несколько значений). " +
                    "Может быть выбрано несколько фильтров по статусу и по всем указанным полям. Если передана " +
                    "пустая строка и отсутствуют фильтры - возвращаются все имеющиеся записи. Также доступно постраничное" +
                    "отображение и сортировка")
    public ResponseEntity<Page<TaskDto>> search(@ParameterObject TaskPaging taskPaging,
                                                @ParameterObject TaskFilterObject taskFilterObject) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.search(taskPaging, taskFilterObject));
    }

    @PatchMapping("/status")
    @Operation(summary = "Перевод задачи в другое состояние",
            description = "Изменить состояние проекта. Переход возможен по согласно следованию статусов" +
                    "слева направо: Новая -> В работе -> Выполнена -> Закрыта")
    public ResponseEntity<String> changeTaskState(@RequestBody ChangeTaskStateDto changeTaskStateDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.changeState(changeTaskStateDto));
    }

    @PostMapping("files/{taskId}")
    @Operation(summary = "Добавить файлы к задаче",
            description = "Добавляет файлы к задаче с переданным id. Можно выбрать несколько файлов для загрузки." +
                    "Максимальный размер одного файла 200 МБ")
    public ResponseEntity<TaskFilesDto> uploadFiles(@PathVariable Long taskId,
                                                    @RequestParam("files") MultipartFile[] files) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body((TaskFilesDto) fileService.upload(taskId, files, INVOKER));
    }

    @GetMapping("files/{taskId}/{filename:.+}")
    @Operation(summary = "Получить файл по ссылке", description = "После получения списка файлов вы можете перейти" +
            "по ссылке, после чего начнется загрузка файла. Данный эндпоинт и служит для этой цели")
    public ResponseEntity<Resource> getFile(@PathVariable Long taskId,
                                            @PathVariable String filename) {
        Resource file = fileService.download(taskId, filename, INVOKER);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("files/{taskId}")
    @Operation(summary = "Получить список всех файлов на проекте",
            description = "Принимает id проекта и возвращает список файлов для данного проекта")
    public ResponseEntity<TaskFilesDto> getFilesList(@PathVariable Long taskId) {
        return ResponseEntity.ok((TaskFilesDto) fileService.getListOfFiles(taskId, INVOKER));
    }

    @PostMapping("/relation")
    @Operation(summary = "Связать задачи",
            description = "Задачи можно связать при условии, что они принадлежат одному проекту.")
    public ResponseEntity<String> linkTasks(@RequestParam Long dependent,
                                            @RequestParam Long dependsOn) {
        return ResponseEntity.ok(((TaskServiceImpl) taskService).linkTasks(dependent, dependsOn));
    }

    @GetMapping("/relation/{id}")
    @Operation(summary = "Получить связанные задачи",
            description = "Получить зависимые и зависящие задачи. При передаче параметров allParents=true и/" +
                    "или allChildren=true будут отображены все 'родители родителей' и 'дети детей' задач.")
    public ResponseEntity<TaskDependencyDto> getTaskRelations(@PathVariable Long id,
                                                              @RequestParam(required = false) boolean allParents,
                                                              @RequestParam(required = false) boolean allChildren) {
        return ResponseEntity.ok(((TaskServiceImpl) taskService).getAllRelatedTasks(id, allParents, allChildren));
    }
}
