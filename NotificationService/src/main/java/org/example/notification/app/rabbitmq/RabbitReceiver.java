package org.example.notification.app.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.example.notification.dto.notification.NotificationRequest;
import org.example.notification.model.Notification;
import org.example.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitReceiver {

   private final NotificationService notificationService;

   @Autowired
   public RabbitReceiver(NotificationService notificationService) {
       this.notificationService = notificationService;
   }

   @RabbitListener(queues = RabbitConfig.TASK_NOTIFICATION_QUEUE, containerFactory = "rabbitListenerContainerFactory")
   public void receiveCreateNotification(NotificationRequest notificationRequest) {
       try {
           Notification notification = notificationService.save(notificationRequest);
           log.info("Получено уведомление (Создание): {}", notification);
       } catch (Exception ex) {
           log.error("Ошибка при обработке уведомления (Создание): {}", ex.getMessage());
           throw ex;
       }
   }

   @RabbitListener(queues = RabbitConfig.TASK_AUDIT_FANOUT_QUEUE)
   public void receiveUpdateNotification(NotificationRequest notificationRequest) {
       Notification notification = notificationService.save(notificationRequest);
       log.info("Получено уведомление (Обновление) с rabbitmq: {}", notification);
   }

   @RabbitListener(queues = RabbitConfig.TASK_NOTIFICATION_TOPIC_QUEUE)
   public void receiveDeleteNotification(NotificationRequest notificationRequest) {
       Notification notification = notificationService.save(notificationRequest);
       log.info("Получено уведомление (Удаление) с rabbitmq: {}", notification);
   }
}
