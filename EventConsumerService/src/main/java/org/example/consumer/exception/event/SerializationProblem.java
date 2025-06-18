package org.example.consumer.exception.event;

import org.example.consumer.model.Event;

import java.time.LocalDateTime;

public class SerializationProblem extends RuntimeException {
    public SerializationProblem (LocalDateTime localDateTime) {
        super(String.format(
                "Проблема с сериализацией, время ошибки: %s", localDateTime
        ));
    }
}