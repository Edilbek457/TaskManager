package org.example.taskFlow.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.taskFlow.app.rabbitmq.RabbitSender;
import org.example.taskFlow.model.Task;
import org.example.taskFlow.model.TaskHistory;
import org.example.taskFlow.repository.TaskHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskHistoryService {

    private final TaskHistoryRepository taskHistoryRepository;
    private final RabbitSender rabbitSender;

    public List<TaskHistory> getTaskHistoryListByTaskId (ObjectId taskId) {
        return taskHistoryRepository.findTasksHistoriesById(taskId);
    }

    public void saveTaskHistory (long taskId, String action, Long performedBy, Map<String, Object> details) {
        TaskHistory history = new TaskHistory();
        history.setTaskId(taskId);
        history.setAction(action);
        history.setPerformedBy(performedBy);
        history.setTimestamp(LocalDateTime.now());
        history.setDetails(details);
        rabbitSender.sendTaskHistory(history);
        taskHistoryRepository.save(history);
    }

    public Map<String, Object> historyChecker(Optional<Task> oldVersion, Optional<Task> newVersion) {
        Map<String, Object> details = new HashMap<>();

        if (oldVersion.isEmpty() && newVersion.isPresent()) {
            details.put("INFO: ", "Создание объекта Task");
            return details;
        } else if (oldVersion.isPresent() && newVersion.isEmpty()) {
            details.put("INFO: ", "Удаление объекта Task");
            return details;
        }

        Task oldTask = oldVersion.get();
        Task newTask = newVersion.get();

        List<String> fieldNames = List.of("Title", "Description", "Status", "Priority", "Deadline", "userId", "projectId");
        List<Object> oldVersionList = Arrays.asList(
                oldTask.getTitle(),
                oldTask.getDescription(),
                oldTask.getStatus(),
                oldTask.getTaskPriority(),
                oldTask.getDeadline(),
                oldTask.getUser() != null ? oldTask.getUser().getId() : null,
                oldTask.getProject() != null ? oldTask.getProject().getId() : null
        );

        List<Object> newVersionList = Arrays.asList(
                newTask.getTitle(),
                newTask.getDescription(),
                newTask.getStatus(),
                newTask.getTaskPriority(),
                newTask.getDeadline(),
                newTask.getUser() != null ? newTask.getUser().getId() : null,
                newTask.getProject() != null ? newTask.getProject().getId() : null
        );


        for (int i = 0; i < oldVersionList.size(); i++) {
            Object oldValue = oldVersionList.get(i);
            Object newValue = newVersionList.get(i);
            if (!Objects.equals(oldValue, newValue)) {
                String field = fieldNames.get(i);
                String changeMessage = String.format("Поле: %s - Старое значение: %s, Новое значение: %s", field, oldValue, newValue);
                details.put("INFO: ", changeMessage);
            }
        } return details;
    }
}
