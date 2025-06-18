package org.example.taskFlow.service;

import lombok.RequiredArgsConstructor;
import org.example.taskFlow.app.kafka.KafkaEvent;
import org.example.taskFlow.dto.comment.CommentUpdateRequest;
import org.example.taskFlow.dto.project.ProjectRequest;
import org.example.taskFlow.dto.task.TaskRequest;
import org.example.taskFlow.dto.user_security.UserUpdateRequest;
import org.example.taskFlow.exception.event.PayloadInitializationException;
import org.example.taskFlow.exception.event.UnknownPayloadTypeException;
import org.example.taskFlow.model.Event;
import org.example.taskFlow.service.payload.CommentPayloadFactory;
import org.example.taskFlow.service.payload.ProjectPayloadFactory;
import org.example.taskFlow.service.payload.TaskPayloadFactory;
import org.example.taskFlow.service.payload.UserPayloadFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final KafkaEvent kafkaEvent;

    public Event createEvent (String eventType, UUID entityId, String entityType, Object firstObject, Object secondObject) {
        String payloadString = payloadGenerator(firstObject, secondObject);
        return new Event(eventType, entityId, entityType, payloadString);
    }

    public void sentKafkaEvent (Event event) {
        kafkaEvent.log(event);
    }

    public String payloadGenerator(Object firstObject, Object secondObject) {
        if (firstObject != null && secondObject == null) {
            return String.format("{Удаление сущности: %s}", firstObject.getClass().getSimpleName());
        } else if (firstObject == null && secondObject != null) {
            return String.format("{Создание сущности: %s}", secondObject.getClass().getSimpleName());
        } else if (firstObject == null && secondObject == null) {
            throw new PayloadInitializationException();
        }

        return switch (firstObject) {
            case ProjectRequest projectRequest when secondObject instanceof ProjectRequest ->
                    new ProjectPayloadFactory().createPayload(firstObject, secondObject);
            case CommentUpdateRequest commentUpdateRequest when secondObject instanceof CommentUpdateRequest ->
                    new CommentPayloadFactory().createPayload(firstObject, secondObject);
            case TaskRequest taskRequest when secondObject instanceof TaskRequest ->
                    new TaskPayloadFactory().createPayload(firstObject, secondObject);
            case UserUpdateRequest userUpdateRequest when secondObject instanceof UserUpdateRequest ->
                    new UserPayloadFactory().createPayload(firstObject, secondObject);
            default -> throw new UnknownPayloadTypeException(firstObject.getClass().getSimpleName());
        };
    }
}

