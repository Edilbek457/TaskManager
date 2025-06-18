package org.example.taskFlow.dto.notification;

import org.example.taskFlow.enums.TaskStatus;

public record NotificationRequest (
        Long taskId,
        String title,
        TaskStatus taskStatus
) {}
