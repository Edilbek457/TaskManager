package org.example.taskFlow.dto.task;

import org.example.taskFlow.enums.TaskPriority;
import org.example.taskFlow.enums.TaskStatus;
import org.example.taskFlow.model.Task;
import java.time.LocalDateTime;

public record TaskResponse (

        long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDateTime deadline,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
    public static TaskResponse from (Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getTaskPriority(),
                task.getDeadline(), task.getCreatedAt(), task.getUpdatedAt());
    }
}
