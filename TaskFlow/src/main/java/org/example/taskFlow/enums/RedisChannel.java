package org.example.taskFlow.enums;

public enum RedisChannel {

    TASK_CREATE("task:create"),
    TASK_UPDATE("task:update"),
    TASK_DELETE("task:delete"),
    USER_CREATE("user:create"),
    USER_UPDATE("user:update"),
    USER_DELETE("user:delete"),
    PROJECT_CREATE("project:create"),
    PROJECT_UPDATE("project:update"),
    PROJECT_DELETE("project:delete");

    private final String channel;

    RedisChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
