package ru.digdes.school.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.digdes.school.dto.FileListCarrier;

public interface FileService {
    FileListCarrier upload(Long id, MultipartFile[] files, String invoker);
    Resource download(Long id, String filename, String invoker);
    FileListCarrier getListOfFiles(Long id, String invoker);
}
