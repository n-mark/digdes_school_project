package ru.digdes.school.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.digdes.school.model.file.TaskFile;

public interface TaskFileRepository extends JpaRepository<TaskFile, Long> {
    boolean existsByFileNameAndAndFilePath(String fileName, String filePath);
}
