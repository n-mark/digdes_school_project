package ru.digdes.school.dto.file;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FileDto {
    Long id;
    String fileName;
    String filePath;
    String urlPath;
}
