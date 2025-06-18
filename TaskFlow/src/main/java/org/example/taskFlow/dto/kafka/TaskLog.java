package org.example.taskFlow.dto.kafka;

import org.example.taskFlow.enums.TaskStatus;
import org.example.taskFlow.model.Task;
import java.io.Serializable;

public record TaskLog(long id, String title, TaskStatus status) implements Serializable {

    public static TaskLog toTaskLog(Task task) {
        return new TaskLog(task.getId(), task.getTitle(), task.getStatus());
    }

}
