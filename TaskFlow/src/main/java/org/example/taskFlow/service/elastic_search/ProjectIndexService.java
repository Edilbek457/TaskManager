package org.example.taskFlow.service.elastic_search;

import lombok.RequiredArgsConstructor;
import org.example.taskFlow.exception.elastic_search.ElasticSearchDocumentNotFoundException;
import org.example.taskFlow.model.Project;
import org.example.taskFlow.model.elastic_search.ProjectIndex;
import org.example.taskFlow.repository.elastic_search.ProjectIndexRepository;
import org.example.taskFlow.service.UUID_Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectIndexService {

    private final ProjectIndexRepository projectIndexRepository;

    public Page<ProjectIndex> searchProjects(
            String query, Pageable pageable) {
        if (query != null && !query.isBlank()) {
            return projectIndexRepository.findByNameContainsOrDescriptionContains(query, query, pageable);
        } return projectIndexRepository.findAll(pageable);
    }

    public static ProjectIndex toProjectIndex(Project project) {
        ProjectIndex projectIndex = new ProjectIndex();
        projectIndex.setId(UUID_Service.fromLong(project.getId()));
        projectIndex.setName(project.getName());
        projectIndex.setDescription(project.getDescription());
        projectIndex.setStatus(project.getStatus());
        return projectIndex;
    }

    public ProjectIndex getProjectIndex(UUID projectIndexId) {
        return projectIndexRepository.findById(projectIndexId).orElseThrow(() -> new ElasticSearchDocumentNotFoundException(projectIndexId));
    }

    public void saveProjectIndex(ProjectIndex projectIndex) {
        projectIndexRepository.save(projectIndex);
    }

    public ProjectIndex updateProjectIndex(UUID id, ProjectIndex projectIndex) {
        ProjectIndex updatedProjectIndex = getProjectIndex(id);
        updatedProjectIndex.setName(projectIndex.getName());
        updatedProjectIndex.setDescription(projectIndex.getDescription());
        updatedProjectIndex.setStatus(projectIndex.getStatus());
        return projectIndexRepository.save(updatedProjectIndex);
    }

    public void deleteProjectIndex(UUID id) {
        projectIndexRepository.deleteById(id);
    }
}
