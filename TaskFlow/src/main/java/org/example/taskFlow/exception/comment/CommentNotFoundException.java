package org.example.taskFlow.exception.comment;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(long commentId) {
        super(String.format(
            "Комментарий по этому Id: %d не найден", commentId
        ));
    }
}
