package org.example.taskFlow.integration.postgres_sql.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.taskFlow.dto.task.TaskRequest;
import org.example.taskFlow.enums.ProjectStatus;
import org.example.taskFlow.enums.TaskPriority;
import org.example.taskFlow.enums.TaskStatus;
import org.example.taskFlow.integration.postgres_sql.AbstractPostgresSQLTest;
import org.example.taskFlow.model.Project;
import org.example.taskFlow.model.Task;
import org.example.taskFlow.model.User;
import org.example.taskFlow.repository.ProjectRepository;
import org.example.taskFlow.repository.TaskRepository;
import org.example.taskFlow.repository.UserRepository;
import org.example.taskFlow.repository.elastic_search.CommentIndexRepository;
import org.example.taskFlow.repository.elastic_search.ProjectIndexRepository;
import org.example.taskFlow.repository.elastic_search.TaskIndexRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TaskControllerTest extends AbstractPostgresSQLTest {

    @BeforeEach
    void ensureTestDatabase() {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        if (!jdbcUrl.contains("test")) {
            throw new IllegalStateException("НЕБЕЗОПАСНО: тест запущен не в тестовой базе!");
        }
    }

    private final static LocalDateTime FIXED_DEADLINE = LocalDateTime.of(2036, 8, 9, 12, 0, 0, 0);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskIndexRepository taskIndexRepository;

    @Autowired
    private ProjectIndexRepository projectIndexRepository;

    @Autowired
    private CommentIndexRepository commentIndexRepository;

    private User createUser()  {
        User user = new User();
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("email@gmail.com");
        user.setActive(true);
        return userRepository.save(user);
    }

    private Project createProject()  {
        Project project = new Project();
        project.setName("Project");
        project.setDescription("Description");
        project.setStatus(ProjectStatus.ARCHIVED);
        project.setOwnerId(UUID.randomUUID());
        return projectRepository.save(project);
    }

    @BeforeEach
    void clean() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        projectRepository.deleteAll();
        taskIndexRepository.deleteAll();
        projectIndexRepository.deleteAll();
        taskIndexRepository.deleteAll();
        projectIndexRepository.deleteAll();
        commentIndexRepository.deleteAll();
    }

    @Test
    public void getAllTasks_whenTasksCount_20_thenGet_20_tasks() throws Exception {
        List<Project> projects = new ArrayList<>();
        List<User> users = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        for (long i = 0; i < 20; i++) {
            User user = new User();
            user.setFirstName("First" + i);
            user.setLastName("Last" + i);
            user.setEmail("email" + i + "@email.com");
            users.add(user);

            Project project = new Project();
            project.setName("Project" + i);
            project.setDescription("Description" + i);
            project.setStatus(ProjectStatus.ARCHIVED);
            project.setOwnerId(UUID.randomUUID());
            projects.add(project);

            Task task = new Task();
            task.setTitle("Task " + i);
            task.setDescription("Description " + i);
            task.setStatus(TaskStatus.TODO);
            task.setTaskPriority(TaskPriority.LOW);
            task.setDeadline(LocalDateTime.now().plusDays(i));
            task.setUser(user);
            task.setProject(project);
            tasks.add(task);
        }

        userRepository.saveAll(users);
        projectRepository.saveAll(projects);
        taskRepository.saveAll(tasks);

        mockMvc.perform(get("/tasks")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(20));
    }

    @Test
    public void getTaskById_whenTaskIsFound_thenGetTaskById() throws Exception {
        Task task = new Task();
        task.setTitle("Task");
        task.setDescription("Description");
        task.setStatus(TaskStatus.TODO);
        task.setTaskPriority(TaskPriority.LOW);
        task.setUser(createUser());
        task.setProject(createProject());
        task.setDeadline(FIXED_DEADLINE);

        Task createdTask = taskRepository.save(task);

        mockMvc.perform(get("/tasks/" + createdTask.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(createdTask.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(task.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(task.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deadline").value(FIXED_DEADLINE.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.priority").value(task.getTaskPriority().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(task.getStatus().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists());
    }

    @Test
    public void createTask_whenTaskIsNew_thenCreateTask() throws Exception {
        TaskRequest taskRequest = new TaskRequest("Title", "Description", TaskStatus.TODO, TaskPriority.LOW, FIXED_DEADLINE, createUser().getId(), createProject().getId());

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(taskRequest.title()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(taskRequest.description()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(taskRequest.status().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.priority").value(taskRequest.priority().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deadline").value(FIXED_DEADLINE.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists());
    }

    @Test
    public void updateTask_whenTaskIsFound_thenUpdateTask() throws Exception {
        User user = createUser();
        Project project = createProject();
        Task existedTask = new Task();
        existedTask.setTitle("Task");
        existedTask.setDescription("Description");
        existedTask.setDeadline(FIXED_DEADLINE);
        existedTask.setStatus(TaskStatus.TODO);
        existedTask.setTaskPriority(TaskPriority.LOW);
        existedTask.setUser(user);
        existedTask.setProject(project);
        Task savedTask = taskRepository.save(existedTask);

        TaskRequest taskRequest = new TaskRequest("NewTitle", "NewDescription", TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM, FIXED_DEADLINE, user.getId(), project.getId());

        mockMvc.perform(put("/tasks/" + savedTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("NewTitle"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("NewDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(TaskStatus.IN_PROGRESS.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.priority").value(TaskPriority.MEDIUM.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deadline").value(FIXED_DEADLINE.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists());
    }

    @Test
    public void deleteTask_whenTaskIsFound_thenDeleteTask() throws Exception {
        Task existedTask = new Task();
        existedTask.setTitle("Task");
        existedTask.setDescription("Description");
        existedTask.setDeadline(FIXED_DEADLINE);
        existedTask.setStatus(TaskStatus.TODO);
        existedTask.setTaskPriority(TaskPriority.LOW);
        existedTask.setUser(createUser());
        existedTask.setProject(createProject());

        Task savedTask = taskRepository.save(existedTask);

        mockMvc.perform(delete("/tasks/" + savedTask.getId()))
                .andExpect(status().isNoContent());
    }
}
