package org.example.taskFlow.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(

        @Size(max = 1500, message = "Комментарий слишком длинный")
        @NotBlank(message = "Комментарий не должен быть пустым")
        String content,

        @NotNull(message = "Комментарий должен быть прикреплен к задаче")
        Long taskId,

        @NotNull(message = "Неопределенный пользователь")
        Long userId
) {
}
