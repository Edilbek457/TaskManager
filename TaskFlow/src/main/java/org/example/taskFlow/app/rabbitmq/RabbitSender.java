package org.example.taskFlow.app.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.example.taskFlow.dto.notification.NotificationRequest;
import org.example.taskFlow.model.TaskHistory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitSender {

    private final AmqpTemplate amqpTemplate;

    @Autowired
    public RabbitSender(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void sendTaskHistory (TaskHistory taskHistory) {
        amqpTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                taskHistory
        );
        log.info("Отправлено taskHistory в rabbitmq: {}", taskHistory);
    }

    public void sendNotification (String exchange, String routing_key, NotificationRequest notificationRequest) {
        amqpTemplate.convertAndSend(
                exchange,
                routing_key,
                notificationRequest
        );
        log.info("Отправлено уведомление в rabbitmq: {}", notificationRequest);
    }
}

