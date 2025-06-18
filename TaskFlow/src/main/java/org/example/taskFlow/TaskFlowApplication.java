package org.example.taskFlow;

import org.example.taskFlow.component.FileProperties;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableRabbit
@SpringBootApplication
@EnableConfigurationProperties(FileProperties.class)
public class TaskFlowApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskFlowApplication.class, args);
    }
}
