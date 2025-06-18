package org.example.taskFlow.exception.user;

public class PasswordNotFoundException extends RuntimeException {
    public PasswordNotFoundException(long userId) {
        super(String.format(
                "Не найден пароль по userId: %d", userId
        ));
    }
}
