package org.example.taskFlow.service.payload;

import org.example.taskFlow.dto.comment.CommentUpdateRequest;
import java.util.Objects;

public class CommentPayloadFactory implements PayloadFactory {
    @Override
    public String createPayload(Object firstObject, Object secondObject) {
        if (!(firstObject instanceof CommentUpdateRequest(String content)) || !(secondObject instanceof CommentUpdateRequest(
                String content1
        ))) {
            throw new IllegalArgumentException("Ожидались объекты типа CommentUpdateRequest");
        }

        return !Objects.equals(content, content1)
                ? "{Старое значение content: " + content + " поменялось на " + content1 + "}"
                : "{}";
    }
}

