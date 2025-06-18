package org.example.taskFlow.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.taskFlow.enums.TaskPriority;
import org.example.taskFlow.enums.TaskStatus;
import org.example.taskFlow.model.Task;
import java.time.LocalDateTime;

public record TaskRequest (

        @NotBlank (message = "Заголовок не может быть пустым")
        String title,

        @NotBlank (message = "Описание не может быть пустым")
        String description,

        @NotNull (message = "Статус не может быть пустым")
        TaskStatus status,

        @NotNull (message = "Задача не может быть без приоритета")
        TaskPriority priority,

        LocalDateTime deadline,

        @NotNull(message = "userId не должен быть пустым для задачи")
        Long userId,

        @NotNull(message = "Id проекта не может быть пустым")
        Long projectId
) {
        public static TaskRequest from (Task task) {
                return new TaskRequest(task.getTitle(), task.getDescription(), task.getStatus(), task.getTaskPriority(), task.getDeadline(), task.getUser().getId(), task.getProject().getId());
        }
}
