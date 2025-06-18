package org.example.taskFlow.app.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.example.taskFlow.model.TaskHistory;
import org.example.taskFlow.repository.TaskHistoryRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitReceiver {

    private TaskHistoryRepository taskHistoryRepository;

    @Autowired
    public RabbitReceiver(TaskHistoryRepository taskHistoryRepository) {
        this.taskHistoryRepository = taskHistoryRepository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void receive(TaskHistory taskHistory) {
        taskHistoryRepository.save(taskHistory);
        log.info("Получено сообщение с rabbitmq: {}", taskHistory);
    }
}


