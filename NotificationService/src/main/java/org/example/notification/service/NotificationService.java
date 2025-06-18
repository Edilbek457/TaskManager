package org.example.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.notification.repository.NotificationRepository;
import org.example.notification.dto.notification.NotificationRequest;
import org.example.notification.model.Notification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification save (NotificationRequest notificationRequest) {
        Notification notification = createNotification(notificationRequest);
        return notificationRepository.save(notification);
    }

    public Notification createNotification (NotificationRequest notificationRequest) {
        Notification notification = new Notification();
        notification.setTaskId(notificationRequest.taskId());
        notification.setTitle(notificationRequest.title());
        notification.setStatus(String.valueOf(notificationRequest.taskStatus()));
        return notification;
    }
}
