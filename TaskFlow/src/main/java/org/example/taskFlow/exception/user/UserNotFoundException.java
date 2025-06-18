package org.example.taskFlow.exception.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super(String.format(
            "Пользователь с id: %d не найден", userId
        ));
    }

    public UserNotFoundException(String email) {
        super(String.format(
                "Пользователь не был найден по email: %s", email
        ));
    }
}
