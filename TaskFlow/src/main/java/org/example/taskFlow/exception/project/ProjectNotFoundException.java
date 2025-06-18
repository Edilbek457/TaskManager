package org.example.taskFlow.exception.project;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(long projectId) {
        super(String.format(
                "Проект по id: %s не найден", projectId
        ));
    }
}
