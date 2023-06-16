package ru.digdes.school.dto.file;

import lombok.*;
import ru.digdes.school.dto.FileListCarrier;
import ru.digdes.school.dto.project.IdNameProjectDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProjectFilesDto implements FileListCarrier {
    private IdNameProjectDto project;
    private List<FileDto> fileList;
}