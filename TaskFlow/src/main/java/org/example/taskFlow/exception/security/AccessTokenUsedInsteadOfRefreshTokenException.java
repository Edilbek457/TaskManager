package org.example.taskFlow.exception.security;

public class AccessTokenUsedInsteadOfRefreshTokenException extends RuntimeException {
    public AccessTokenUsedInsteadOfRefreshTokenException() {
        super("Нельзя обновить токен по access токену — используйте refresh токен");
    }
}
