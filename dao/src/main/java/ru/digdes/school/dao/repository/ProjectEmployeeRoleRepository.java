package ru.digdes.school.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.digdes.school.model.team.ProjectEmployeeRole;

import java.util.List;

public interface ProjectEmployeeRoleRepository extends JpaRepository<ProjectEmployeeRole, Long> {
    ProjectEmployeeRole findByProjectIdAndEmployeeId(Long projectId, Long employeeId);
    List<ProjectEmployeeRole> findByProjectId(Long projectId);
    boolean existsByProjectIdAndEmployeeId(Long projectId, Long employeeId);
}
