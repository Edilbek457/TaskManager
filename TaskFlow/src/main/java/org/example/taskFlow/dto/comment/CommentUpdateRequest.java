package org.example.taskFlow.dto.comment;

import jakarta.validation.constraints.Size;
import org.example.taskFlow.model.Comment;

public record CommentUpdateRequest (

        @Size(max = 1500, message = "Комментарий слишком длинный")
        String content
) {
        public static CommentUpdateRequest from(Comment comment) {
                return new CommentUpdateRequest(comment.getContent());
        }
}
