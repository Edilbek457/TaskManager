package org.example.notification.dto.notification;

import org.example.notification.enums.TaskStatus;

public record NotificationRequest (
        Long taskId,
        String title,
        TaskStatus taskStatus
) {}

