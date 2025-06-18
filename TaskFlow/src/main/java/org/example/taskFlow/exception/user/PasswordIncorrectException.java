package org.example.taskFlow.exception.user;

public class PasswordIncorrectException extends RuntimeException {
    public PasswordIncorrectException() {
        super("Был введен неверный пароль");
    }
}
