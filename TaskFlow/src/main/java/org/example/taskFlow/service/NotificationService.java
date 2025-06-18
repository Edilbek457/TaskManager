package org.example.taskFlow.service;

import lombok.RequiredArgsConstructor;
import org.example.taskFlow.app.rabbitmq.RabbitSender;
import org.example.taskFlow.dto.notification.NotificationRequest;
import org.example.taskFlow.model.Task;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RabbitSender rabbitSender;

    public NotificationRequest taskToNotificationRequest(Task task) {
        return new NotificationRequest(task.getId(), task.getTitle(), task.getStatus());
    }

    public void pushNotificationToRabbitMq (String exchange, String routing_key, NotificationRequest notificationRequest) {
        rabbitSender.sendNotification(exchange, routing_key, notificationRequest);
    }

}
