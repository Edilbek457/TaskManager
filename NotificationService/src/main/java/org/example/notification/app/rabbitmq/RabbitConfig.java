package org.example.notification.app.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_DIRECT = "taskflow.direct.exchange";
    public static final String EXCHANGE_FANOUT = "taskflow.fanout.exchange";
    public static final String EXCHANGE_TOPIC = "taskflow.topic.exchange";
    public static final String EXCHANGE_HEADERS = "taskflow.headers.exchange";

    public static final String TASK_NOTIFICATION_QUEUE = "task.notifications.direct";
    public static final String TASK_AUDIT_FANOUT_QUEUE = "task.audit.fanout";
    public static final String TASK_NOTIFICATION_TOPIC_QUEUE = "task.notifications.topic";
    public static final String TASK_ERROR_TOPIC_QUEUE = "task.error.topic";
    public static final String TASK_NOTIFICATION_HEADERS_QUEUE = "task.notifications.headers";

    public static final String DLX_EXCHANGE = "taskflow.dlx.exchange";
    public static final String DLX_QUEUE = "task.dlx.notifications";

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue dlxQueue() {
        return new Queue(DLX_QUEUE, true);
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue())
                .to(dlxExchange())
                .with("dlx.routing.key");
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_DIRECT);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(EXCHANGE_FANOUT);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE_TOPIC);
    }

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(EXCHANGE_HEADERS);
    }

    @Bean
    public Queue taskNotificationQueue() {
        return QueueBuilder.durable(TASK_NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dlx.routing.key")
                .build();
    }


    @Bean
    public Queue taskAuditFanoutQueue() {
        return new Queue(TASK_AUDIT_FANOUT_QUEUE, true);
    }

    @Bean
    public Queue taskNotificationTopicQueue() {
        return new Queue(TASK_NOTIFICATION_TOPIC_QUEUE, true);
    }

    @Bean
    public Queue taskErrorTopicQueue() {
        return new Queue(TASK_ERROR_TOPIC_QUEUE, true);
    }

    @Bean
    public Queue taskNotificationHeadersQueue() {
        return new Queue(TASK_NOTIFICATION_HEADERS_QUEUE, true);
    }


    @Bean
    public Binding fanoutBindingAuditQueue() {
        return BindingBuilder.bind(taskAuditFanoutQueue()).to(fanoutExchange());
    }

    @Bean
    public Binding topicBindingNotificationQueue() {
        return BindingBuilder.bind(taskNotificationTopicQueue())
                .to(topicExchange())
                .with("task.notifications.*");
    }

    @Bean
    public Binding topicBindingErrorQueue() {
        return BindingBuilder.bind(taskErrorTopicQueue())
                .to(topicExchange())
                .with("task.error.#");
    }

    @Bean
    public Binding directBindingNotificationQueue() {
        return BindingBuilder.bind(taskNotificationQueue())
                .to(directExchange())
                .with("task.notifications");
    }

    @Bean
    public Binding headersBindingNotificationQueue() {
        return BindingBuilder.bind(taskNotificationHeadersQueue())
                .to(headersExchange())
                .where("format").matches("pdf");
    }

    @Bean public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        FixedBackOffPolicy backOff = new FixedBackOffPolicy();
        backOff.setBackOffPeriod(3);

        SimpleRetryPolicy policy = new SimpleRetryPolicy();
        policy.setMaxAttempts(2000);

        RetryTemplate template = new RetryTemplate();
        template.setBackOffPolicy(backOff);
        template.setRetryPolicy(policy);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf,
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            RabbitTemplate rabbitTemplate
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, cf);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setAdviceChain(
                RetryInterceptorBuilder
                        .stateless()
                        .retryOperations(retryTemplate())
                        .recoverer(new RepublishMessageRecoverer(
                                rabbitTemplate, "taskflow.dlx.exchange", "task.dlx.notifications"))
                        .build()
        );
        return factory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory cf) {
        RabbitAdmin admin = new RabbitAdmin(cf);
        admin.setIgnoreDeclarationExceptions(true);
        return admin;
    }
}
