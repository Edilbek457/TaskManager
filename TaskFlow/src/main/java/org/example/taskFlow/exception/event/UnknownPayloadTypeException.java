package org.example.taskFlow.exception.event;

public class UnknownPayloadTypeException extends RuntimeException {
    public UnknownPayloadTypeException(String typeName) {
        super("Неизвестный тип передаваемый для payload: " + typeName);
    }
}

