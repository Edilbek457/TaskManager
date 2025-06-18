package org.example.taskFlow.dto.kafka;

import org.example.taskFlow.model.Comment;
import java.io.Serializable;

public record CommentLog(long id, long taskId, long userId) implements Serializable {

    public static CommentLog toCommentLog(Comment comment) {
        return new CommentLog(comment.getId(), comment.getTask().getId(), comment.getUser().getId());
    }

}
