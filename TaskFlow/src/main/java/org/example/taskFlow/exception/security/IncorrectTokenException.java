package org.example.taskFlow.exception.security;

public class IncorrectTokenException extends RuntimeException {
    public IncorrectTokenException() {
        super("Не правильный формат токена или неверный токен");
    }
}
