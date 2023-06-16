package ru.digdes.school.dto.file;

import lombok.*;
import ru.digdes.school.dto.FileListCarrier;
import ru.digdes.school.dto.task.IdNameTaskDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TaskFilesDto implements FileListCarrier {
    private IdNameTaskDto idNameTaskDto;
    private List<FileDto> fileList;
}
