package org.example.taskFlow.exception.security;

public class PrivilegeExceededException extends RuntimeException {
    public PrivilegeExceededException() {
        super("Нельзя выдать доступ на роль админа");
    }
}
