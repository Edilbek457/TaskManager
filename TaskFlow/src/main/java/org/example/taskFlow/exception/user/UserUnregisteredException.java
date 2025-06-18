package org.example.taskFlow.exception.user;

public class UserUnregisteredException extends RuntimeException {
    public UserUnregisteredException(String email) {
        super(String.format(
            "Пользователь не был зарегистрирован по Email: %s", email
        ));
    }
}
