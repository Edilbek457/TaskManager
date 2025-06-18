package org.example.taskFlow.app.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {

    public static final String QUEUE = "task-history-queue";
    public static final String TEST_QUEUE = "task-history-test-queue";
    public static final String EXCHANGE = "task-history-exchange";
    public static final String ROUTING_KEY = "task-history";

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public Queue taskHistoryQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 20_000);
        args.put("x-dead-letter-exchange", "dlx-exchange");
        return new Queue(QUEUE, true, false, false, args);
    }

    @Bean
    public Queue taskHistoryTestQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 20_000);
        args.put("x-dead-letter-exchange", "dlx-exchange");
        return new Queue(TEST_QUEUE, true, false, false, args);
    }

    @Bean
    public DirectExchange taskHistoryExchange() {
        return new DirectExchange(EXCHANGE);
    }


    @Bean
    public Binding taskHistoryBinding(Queue taskHistoryQueue, DirectExchange taskHistoryExchange) {
        return BindingBuilder
                .bind(taskHistoryQueue)
                .to(taskHistoryExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding taskHistoryTestBinding(Queue taskHistoryTestQueue, DirectExchange taskHistoryExchange) {
        return BindingBuilder
                .bind(taskHistoryTestQueue)
                .to(taskHistoryExchange)
                .with(ROUTING_KEY);
    }


    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}


