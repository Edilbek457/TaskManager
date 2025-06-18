package org.example.taskFlow.exception.task;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException (long taskId) {
        super(String.format(
                "Задача с Id: %d не найдена", taskId
        ));
    }
}
