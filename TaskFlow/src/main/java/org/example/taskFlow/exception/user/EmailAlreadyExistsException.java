package org.example.taskFlow.exception.user;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String userEmail) {
        super(String.format(
                "Пользователь с email-ом %s уже существует", userEmail
        ));
    }
}
