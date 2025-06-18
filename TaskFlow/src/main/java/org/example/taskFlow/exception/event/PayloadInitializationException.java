package org.example.taskFlow.exception.event;

public class PayloadInitializationException extends RuntimeException {
    public PayloadInitializationException() {
        super("Не удалось инициализировать поле payload для Event");
    }
}

