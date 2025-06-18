package org.example.taskFlow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskFlow.app.redis.RedisPublisher;
import org.example.taskFlow.dto.notification.NotificationRequest;
import org.example.taskFlow.dto.task.TaskRequest;
import org.example.taskFlow.enums.LogLevel;
import org.example.taskFlow.enums.RedisChannel;
import org.example.taskFlow.exception.task.TaskNotFoundException;
import org.example.taskFlow.model.Event;
import org.example.taskFlow.model.Task;
import org.example.taskFlow.repository.TaskRepository;
import org.example.taskFlow.service.elastic_search.TaskIndexService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisPublisher redisPublisher;
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ProjectService projectService;
    private final TaskHistoryService taskHistoryService;
    private final NotificationService notificationService;
    private final LogService logEventService;
    private final EventService eventService;
    private final TaskIndexService taskIndexService;

    public Page<Task> getAllTasks (Pageable pageable) {
        Page<Task> tasks = taskRepository.findAll(pageable);
        log.info("Получение всех задач, Размер списка задач: {}", tasks.getTotalElements());
        return tasks;
    }

    public Task getTaskById(long id) {
        Object cachedTask = redisTemplate.opsForValue().get(String.valueOf(id));
        if (cachedTask != null) {
            log.info("Получено из Redis: задача id {}", id);
            return objectMapper.convertValue(cachedTask, Task.class);
        }

        Optional<Task> task = taskRepository.findTaskById(id);

        if (task.isPresent()) {
            Task foundTask = task.get();
            redisTemplate.opsForValue().set(String.valueOf(id), foundTask, 30, TimeUnit.MINUTES);
            log.info("Получено из БД и сохранено в Redis: задача id {}", id);
            return foundTask;
        } else {
            throw new TaskNotFoundException(id);
        }
    }


    public Task saveTask (TaskRequest taskRequest) {
        Task task = createTask(taskRequest);
        task.setUser(userService.getUserById(taskRequest.userId()));
        task.setProject(projectService.getProjectById(taskRequest.projectId()));
        Task savedTask = taskRepository.save(task);

        taskIndexService.saveTaskIndex(TaskIndexService.toTaskIndex(task));
        redisTemplate.opsForValue().set(String.valueOf(task.getId()), savedTask, 30, TimeUnit.MINUTES);

        NotificationRequest notificationRequest = notificationService.taskToNotificationRequest(savedTask);
        notificationService.pushNotificationToRabbitMq("taskflow.direct.exchange", "task.notifications", notificationRequest);
        taskHistoryService.saveTaskHistory(savedTask.getId(),"CREATE", -1L, taskHistoryService.historyChecker(Optional.empty(), Optional.of(savedTask)));
        logEventService.createAndSendLog(LogLevel.INFO, "Task saved", savedTask);
        Event createdEvent = eventService.createEvent("TASK_CREATED", UUID_Service.fromLong(savedTask.getId()), "TASK", null, savedTask);
        eventService.sentKafkaEvent(createdEvent);
        redisPublisher.publish(RedisChannel.TASK_CREATE.getChannel(), createdEvent);
        return savedTask;
    }

    public Task updateTask (long id, TaskRequest taskRequest) {
        Task task = getTaskById(id);
        Task oldTask = getTask(task);

        task.setTitle(taskRequest.title());
        task.setDescription(taskRequest.description());
        task.setStatus(taskRequest.status());
        task.setTaskPriority(taskRequest.priority());
        task.setDeadline(taskRequest.deadline());
        task.setUser(userService.getUserById(taskRequest.userId()));
        task.setProject(projectService.getProjectById(taskRequest.projectId()));
        Task updatedTask = taskRepository.save(task);

        taskIndexService.updateTaskIndex(UUID_Service.fromLong(updatedTask.getId()), TaskIndexService.toTaskIndex(updatedTask));
        redisTemplate.opsForValue().set(String.valueOf(id), updatedTask, 30, TimeUnit.MINUTES);

        NotificationRequest notificationRequest = notificationService.taskToNotificationRequest(updatedTask);
        notificationService.pushNotificationToRabbitMq("taskflow.fanout.exchange", "", notificationRequest);
        taskHistoryService.saveTaskHistory(updatedTask.getId(), "UPDATE", -1L, taskHistoryService.historyChecker(Optional.of(oldTask), Optional.of(updatedTask)));
        logEventService.createAndSendLog(LogLevel.INFO, "Task updated", updatedTask);
        Event createdEvent = eventService.createEvent("TASK_UPDATED", UUID_Service.fromLong(updatedTask.getId()), "TASK", TaskRequest.from(oldTask), taskRequest);
        eventService.sentKafkaEvent(createdEvent);
        redisPublisher.publish(RedisChannel.TASK_UPDATE.getChannel(), createdEvent);
        return updatedTask;
    }


    public void deleteTask (long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.deleteById(id);

        taskIndexService.deleteTaskIndex(UUID_Service.fromLong(task.getId()));
        redisTemplate.delete(String.valueOf(task.getId()));

        NotificationRequest notificationRequest = notificationService.taskToNotificationRequest(task);
        notificationService.pushNotificationToRabbitMq("taskflow.topic.exchange", "task.notifications.*", notificationRequest);
        taskHistoryService.saveTaskHistory(task.getId(), "DELETE", -1L, taskHistoryService.historyChecker(Optional.of(task), Optional.empty()));
        logEventService.createAndSendLog(LogLevel.INFO, "Task deleted", task);
        Event createdEvent = eventService.createEvent("TASK_DELETED", UUID_Service.fromLong(task.getId()), "TASK", task, null);
        eventService.sentKafkaEvent(createdEvent);
        redisPublisher.publish(RedisChannel.TASK_DELETE.getChannel(), createdEvent);
    }

    public Task createTask ( TaskRequest taskRequest ) {
        Task task = new Task();
        task.setTitle(taskRequest.title());
        task.setDescription(taskRequest.description());
        task.setStatus(taskRequest.status());
        task.setTaskPriority(taskRequest.priority());
        task.setDeadline(taskRequest.deadline());
        return task;
    }

    private static Task getTask(Task task) {
        Task oldTask = new Task();
        oldTask.setId(task.getId());
        oldTask.setTitle(task.getTitle());
        oldTask.setDescription(task.getDescription());
        oldTask.setStatus(task.getStatus());
        oldTask.setTaskPriority(task.getTaskPriority());
        oldTask.setDeadline(task.getDeadline());
        oldTask.setCreatedAt(task.getCreatedAt());
        oldTask.setUpdatedAt(task.getUpdatedAt());
        oldTask.setUser(task.getUser());
        oldTask.setProject(task.getProject());
        return oldTask;
    }
}

