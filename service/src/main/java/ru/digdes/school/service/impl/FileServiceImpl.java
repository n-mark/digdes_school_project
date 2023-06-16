package ru.digdes.school.service.impl;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.digdes.school.dao.repository.ProjectFileRepository;
import ru.digdes.school.dao.repository.ProjectRepository;
import ru.digdes.school.dao.repository.TaskFileRepository;
import ru.digdes.school.dao.repository.TaskRepository;
import ru.digdes.school.dto.FileListCarrier;
import ru.digdes.school.dto.file.FileDto;
import ru.digdes.school.dto.file.ProjectFilesDto;
import ru.digdes.school.dto.file.TaskFilesDto;
import ru.digdes.school.dto.project.IdNameProjectDto;
import ru.digdes.school.dto.task.IdNameTaskDto;
import ru.digdes.school.model.file.ProjectFile;
import ru.digdes.school.model.file.TaskFile;
import ru.digdes.school.model.project.Project;
import ru.digdes.school.model.task.Task;
import ru.digdes.school.service.FileService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    public static final String BASE_PATH = System.getProperty("user.dir");
    public static final String CURRENT_BASE_DIR = "/files/";
    private final ProjectFileRepository projectFileRepository;
    private final ProjectRepository projectRepository;
    private final TaskFileRepository taskFileRepository;
    private final TaskRepository taskRepository;

    public FileServiceImpl(ProjectFileRepository projectFileRepository,
                           ProjectRepository projectRepository,
                           TaskFileRepository taskFileRepository,
                           TaskRepository taskRepository) {
        this.projectFileRepository = projectFileRepository;
        this.projectRepository = projectRepository;
        this.taskFileRepository = taskFileRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional
    public FileListCarrier upload(Long id, MultipartFile[] files, String invoker) {
        switch (invoker) {
            case "projects" -> {
                return uploadForProject(id, files, invoker);
            }
            case "tasks" -> {
                return uploadForTask(id, files, invoker);
            }
            default -> {
                throw new IllegalArgumentException("Can't define the invoker");
            }
        }
    }

    @Override
    public Resource download(Long id, String filename, String invoker) {
        Path path = Paths.get(BASE_PATH + CURRENT_BASE_DIR + invoker + "/" + id + "/" + filename);
        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("Cannot retrieve the file. Probably it doesn't exist");
            }
        } catch (MalformedURLException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileListCarrier getListOfFiles(Long id, String invoker) {
        switch (invoker) {
            case "projects" -> {
                return getListForProject(id);
            }
            case "tasks" -> {
                return getListForTask(id);
            }
            default -> {
                throw new IllegalArgumentException("Can't define the invoker");
            }
        }
    }

    private FileListCarrier getListForTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new IllegalArgumentException("The task doesn't exist");
        }

        String currentHost = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        TaskFilesDto taskFilesDto = new TaskFilesDto();
        Task task = taskRepository.getReferenceById(id);
        IdNameTaskDto taskDto = new IdNameTaskDto(task.getId(), task.getTaskName());
        taskFilesDto.setIdNameTaskDto(taskDto);

        taskFilesDto.setFileList(task.getFiles().stream()
                .map(file -> new FileDto(
                        file.getId(),
                        file.getFileName(),
                        file.getFilePath(),
                        currentHost + file.getUrlPath()))
                .toList());

        return taskFilesDto;
    }

    private FileListCarrier getListForProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new IllegalArgumentException("The project doesn't exist");
        }

        String currentHost = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        ProjectFilesDto projectFilesDto = new ProjectFilesDto();
        Project project = projectRepository.getReferenceById(id);
        IdNameProjectDto projectDto = new IdNameProjectDto(project.getId(), project.getProjectName());
        projectFilesDto.setProject(projectDto);

        projectFilesDto.setFileList(project.getFiles().stream()
                .map(file -> new FileDto(
                        file.getId(),
                        file.getFileName(),
                        file.getFilePath(),
                        currentHost + file.getUrlPath()))
                .toList());

        return projectFilesDto;
    }

    private FileListCarrier uploadForProject(Long id, MultipartFile[] files, String invoker) {
        if (!projectRepository.existsById(id)) {
            throw new IllegalArgumentException("Can't upload files - the project doesn't exist");
        }
        ProjectFilesDto projectFilesDto = new ProjectFilesDto();

        Project project = projectRepository.getReferenceById(id);
        IdNameProjectDto projectDto = new IdNameProjectDto(project.getId(), project.getProjectName());
        projectFilesDto.setProject(projectDto);

        List<FileDto> fileList = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();

            try {
                String subFolder = CURRENT_BASE_DIR + invoker + "/" + id;
                String filePath = BASE_PATH + subFolder + "/" + fileName;
                String urlPath = "/" + invoker + CURRENT_BASE_DIR + id + "/" + fileName;

                if (projectFileRepository.existsByFileNameAndAndFilePath(fileName, subFolder)
                        && Files.exists(Paths.get(BASE_PATH + subFolder + "/" + fileName))) {
                    throw new IllegalArgumentException("The project already contains the file " + fileName);
                }

                Files.createDirectories(Paths.get(BASE_PATH + subFolder));

                Files.write(Paths.get(filePath), file.getBytes());

                ProjectFile projectFile = new ProjectFile();
                projectFile.setFileName(fileName);
                projectFile.setFilePath(subFolder);
                projectFile.setUrlPath(urlPath);
                projectFile.setProject(project);

                ProjectFile savedProjectFile = projectFileRepository.save(projectFile);

                FileDto fileDto = new FileDto();
                fileDto.setId(savedProjectFile.getId());
                fileDto.setFileName(savedProjectFile.getFileName());
                fileDto.setFilePath(savedProjectFile.getFilePath());
                String currentHost = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                fileDto.setUrlPath(currentHost + savedProjectFile.getUrlPath());

                fileList.add(fileDto);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        projectFilesDto.setFileList(fileList);

        return projectFilesDto;
    }

//TODO: убрать дублирование кода.
    private FileListCarrier uploadForTask(Long id, MultipartFile[] files, String invoker) {
        if (!taskRepository.existsById(id)) {
            throw new IllegalArgumentException("Can't upload files - the task doesn't exist");
        }
        TaskFilesDto taskFilesDto = new TaskFilesDto();

        Task task = taskRepository.getReferenceById(id);
        IdNameTaskDto taskDto = new IdNameTaskDto(task.getId(), task.getTaskName());
        taskFilesDto.setIdNameTaskDto(taskDto);

        List<FileDto> fileList = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();

            try {
                String subFolder = CURRENT_BASE_DIR + invoker + "/" + id;
                String filePath = BASE_PATH + subFolder + "/" + fileName;
                String urlPath = "/" + invoker + CURRENT_BASE_DIR + id + "/" + fileName;

                if (taskFileRepository.existsByFileNameAndAndFilePath(fileName, subFolder)
                        && Files.exists(Paths.get(BASE_PATH + subFolder + "/" + fileName))) {
                    throw new IllegalArgumentException("The project already contains the file " + fileName);
                }

                Files.createDirectories(Paths.get(BASE_PATH + subFolder));

                Files.write(Paths.get(filePath), file.getBytes());

                TaskFile taskFile = new TaskFile();
                taskFile.setFileName(fileName);
                taskFile.setFilePath(subFolder);
                taskFile.setUrlPath(urlPath);
                taskFile.setTask(task);

                TaskFile savedTaskFile = taskFileRepository.save(taskFile);

                FileDto fileDto = new FileDto();
                fileDto.setId(savedTaskFile.getId());
                fileDto.setFileName(savedTaskFile.getFileName());
                fileDto.setFilePath(savedTaskFile.getFilePath());
                String currentHost = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                fileDto.setUrlPath(currentHost + savedTaskFile.getUrlPath());

                fileList.add(fileDto);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        taskFilesDto.setFileList(fileList);

        return taskFilesDto;
    }

}
