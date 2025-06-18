package org.example.taskFlow.repository.elastic_search;

import org.example.taskFlow.enums.TaskPriority;
import org.example.taskFlow.enums.TaskStatus;
import org.example.taskFlow.model.elastic_search.TaskIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TaskIndexRepository extends ElasticsearchRepository<TaskIndex, UUID> {
    Page<TaskIndex> findByTitleContainsOrDescriptionContains(String title, String description, Pageable pageable);
    Page<TaskIndex> findByStatusAndPriority(TaskStatus status, TaskPriority priority, Pageable pageable);
    long countByStatus(TaskStatus status);
    long countByPriority(TaskPriority priority);
}
