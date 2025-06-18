package org.example.taskFlow.dto.comment;

import org.example.taskFlow.model.Comment;
import org.example.taskFlow.model.Task;
import org.example.taskFlow.model.User;
import java.time.LocalDateTime;

public record CommentResponse (

        long id,
        String content,
        Task task,
        User user,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
    public static CommentResponse from (Comment comment) {
        return new CommentResponse(comment.getId(), comment.getContent(), comment.getTask(), comment.getUser(), comment.getCreatedAt(), comment.getUpdatedAt());
    }
}
