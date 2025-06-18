package org.example.taskFlow.service.elastic_search;

import lombok.RequiredArgsConstructor;
import org.example.taskFlow.enums.TaskPriority;
import org.example.taskFlow.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.example.taskFlow.exception.elastic_search.ElasticSearchDocumentNotFoundException;
import org.example.taskFlow.model.Task;
import org.example.taskFlow.model.elastic_search.TaskIndex;
import org.example.taskFlow.repository.elastic_search.TaskIndexRepository;
import org.example.taskFlow.service.UUID_Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskIndexService {

    private final TaskIndexRepository taskIndexRepository;

    public Page<TaskIndex> searchTasks(
            String query,
            String status,
            String priority,
            Pageable pageable
    ) {
        if (status != null && priority != null) {
            TaskStatus ts = TaskStatus.valueOf(status);
            TaskPriority tp = TaskPriority.valueOf(priority);
        return taskIndexRepository.findByStatusAndPriority(ts, tp, pageable);
        }
        if (query != null && !query.isBlank()) {
            return taskIndexRepository.findByTitleContainsOrDescriptionContains(query, query, pageable);
        }
        return taskIndexRepository.findAll(pageable);
    }

    public static TaskIndex toTaskIndex(Task task) {
        TaskIndex taskIndex = new TaskIndex();
        taskIndex.setId(UUID_Service.fromLong(task.getId()));
        taskIndex.setTitle(task.getTitle());
        taskIndex.setDescription(task.getDescription());
        taskIndex.setStatus(task.getStatus());
        taskIndex.setPriority(task.getTaskPriority());
        taskIndex.setDeadline(task.getDeadline());
        taskIndex.setAssignedUserId(UUID_Service.fromLong(task.getUser().getId()));
        taskIndex.setProjectId(UUID_Service.fromLong(task.getProject().getId()));
        return taskIndex;
    }

    public TaskIndex getTaskIndexById(UUID taskIndexId) {
        return taskIndexRepository.findById(taskIndexId).orElseThrow(() -> new ElasticSearchDocumentNotFoundException(taskIndexId));
    }

    public void saveTaskIndex(TaskIndex taskIndex) {
        taskIndexRepository.save(taskIndex);
    }

    public void updateTaskIndex(UUID id, TaskIndex taskIndex) {
        TaskIndex updatedTaskIndex = getTaskIndexById(id);
        updatedTaskIndex.setTitle(taskIndex.getTitle());
        updatedTaskIndex.setDescription(taskIndex.getDescription());
        updatedTaskIndex.setStatus(taskIndex.getStatus());
        updatedTaskIndex.setPriority(taskIndex.getPriority());
        updatedTaskIndex.setDeadline(taskIndex.getDeadline());
        updatedTaskIndex.setAssignedUserId(taskIndex.getAssignedUserId());
        updatedTaskIndex.setProjectId(taskIndex.getProjectId());
        taskIndexRepository.save(updatedTaskIndex);
    }

    public void deleteTaskIndex(UUID id) {
        taskIndexRepository.deleteById(id);
    }
}
