package ru.digdes.school.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.digdes.school.dao.repository.ProjectRepository;
import ru.digdes.school.dao.repository.specifications.SpecificationFiltering;
import ru.digdes.school.dto.CanDoFiltering;
import ru.digdes.school.dto.CanDoPaging;
import ru.digdes.school.dto.Stateable;
import ru.digdes.school.dto.project.ChangeProjectStateDto;
import ru.digdes.school.dto.project.ProjectDto;
import ru.digdes.school.dto.project.ProjectFilterObject;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.model.project.Project;
import ru.digdes.school.model.project.ProjectStatus;
import ru.digdes.school.service.BasicService;

import java.util.UUID;

@Service
public class ProjectServiceImpl implements BasicService<ProjectDto> {
    private final ProjectRepository projectRepository;
    private final Mapper<Project, ProjectDto> mapper;
    private final SpecificationFiltering<Project> specificationFiltering;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              Mapper<Project, ProjectDto> mapper,
                              SpecificationFiltering<Project> specificationFiltering) {
        this.projectRepository = projectRepository;
        this.mapper = mapper;
        this.specificationFiltering = specificationFiltering;
    }


    @Override
    public ProjectDto create(ProjectDto createFrom) {
        if (createFrom.getProjectName() == null) {
            throw new IllegalArgumentException("The name of the project cannot be null");
        }

        Project project = mapper.dtoToModel(createFrom);
        do {
            project.setProjectCode(UUID.randomUUID().toString());
        } while (projectRepository.existsByProjectCode(project.getProjectCode()));
        project.setProjectStatus(ProjectStatus.DRAFT);

        return mapper.modelToDto(projectRepository.save(project));
    }

    @Override
    public ProjectDto update(ProjectDto updateFrom) {
        if (!projectRepository.existsById(updateFrom.getId())) {
            throw new EntityNotFoundException("The project with id = " + updateFrom.getId() + " doesn't exist");
        }
        Project project = projectRepository.getReferenceById(updateFrom.getId());
        mapper.updateMerge(project, updateFrom);
        return mapper.modelToDto(projectRepository.save(project));
    }

    @Override
    public Page<ProjectDto> search(CanDoPaging pagingObject, CanDoFiltering filteringObject) {
        ProjectFilterObject projectFilterObject = (ProjectFilterObject) filteringObject;
        String[] keyWords = projectFilterObject.getSearchString().orElse("").split(" ");

        Pageable pageable = PageRequest.of(pagingObject.getPageNumber(), pagingObject.getPageSize());

        Page<Project> allFound = projectRepository.findAll(
                Specification.where(specificationFiltering.withAllKeywords(keyWords))
                        .and(specificationFiltering.withFilters(filteringObject))
                        .and(specificationFiltering.setSorting(pagingObject.getSortDirection(), pagingObject.getSortBy())),
                pageable
        );

        return allFound.map(mapper::modelToDto);
    }

    @Override
    public String changeState(Stateable changeStateObject) {
        ChangeProjectStateDto changeProjectStateDto = (ChangeProjectStateDto) changeStateObject;
        if (!projectRepository.existsById(changeProjectStateDto.getId())) {
            throw new EntityNotFoundException("The project with id = " + changeProjectStateDto.getId() + " doesn't exist");
        }

        Project project = projectRepository.getReferenceById(changeProjectStateDto.getId());

        if (project.getProjectStatus().compareTo(changeProjectStateDto.getProjectStatus()) >= 0) {
            throw new IllegalArgumentException("Wrong assignment. The project with status '" + project.getProjectStatus() +
                    "' cannot be set to status '" + changeProjectStateDto.getProjectStatus() + "'. The proper project status " +
                    "sequence is 'DRAFT' -> 'IN_WORK' -> 'TESTING' -> 'COMPLETED'");
        }

        project.setProjectStatus(changeProjectStateDto.getProjectStatus());
        projectRepository.save(project);
        return "The project with id = " + project.getId() + " status were successfully changed to " + project.getProjectStatus();
    }
}
