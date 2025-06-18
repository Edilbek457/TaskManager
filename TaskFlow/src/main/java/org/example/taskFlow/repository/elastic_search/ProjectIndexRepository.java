package org.example.taskFlow.repository.elastic_search;

import org.example.taskFlow.enums.ProjectStatus;
import org.example.taskFlow.model.elastic_search.ProjectIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProjectIndexRepository extends ElasticsearchRepository<ProjectIndex, UUID> {
    Page<ProjectIndex> findByNameContainsOrDescriptionContains(String name, String description, Pageable pageable);
    long countByStatus(ProjectStatus status);
}
