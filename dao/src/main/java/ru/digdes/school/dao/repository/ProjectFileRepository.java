package ru.digdes.school.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.digdes.school.model.file.ProjectFile;

public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long> {
    boolean existsByFileNameAndAndFilePath(String fileName, String filePath);
}
