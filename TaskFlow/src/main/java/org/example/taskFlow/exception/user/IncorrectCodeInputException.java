package org.example.taskFlow.exception.user;

public class IncorrectCodeInputException extends RuntimeException {
    public IncorrectCodeInputException() {
        super("Введен неправильный код подтверждения");
    }
}
