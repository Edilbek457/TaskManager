package org.example.taskFlow.integration.elastic_search.controller;

import org.example.taskFlow.dto.comment.CommentCreateRequest;
import org.example.taskFlow.dto.project.ProjectRequest;
import org.example.taskFlow.dto.task.TaskRequest;
import org.example.taskFlow.dto.user.UserRequest;
import org.example.taskFlow.enums.ProjectStatus;
import org.example.taskFlow.enums.TaskPriority;
import org.example.taskFlow.enums.TaskStatus;
import org.example.taskFlow.integration.elastic_search.AbstractElasticsearchTest;
import org.example.taskFlow.model.Comment;
import org.example.taskFlow.model.Project;
import org.example.taskFlow.model.Task;
import org.example.taskFlow.model.User;
import org.example.taskFlow.repository.CommentRepository;
import org.example.taskFlow.repository.ProjectRepository;
import org.example.taskFlow.repository.TaskRepository;
import org.example.taskFlow.repository.UserRepository;
import org.example.taskFlow.repository.elastic_search.CommentIndexRepository;
import org.example.taskFlow.repository.elastic_search.ProjectIndexRepository;
import org.example.taskFlow.repository.elastic_search.TaskIndexRepository;
import org.example.taskFlow.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SearchControllerTest extends AbstractElasticsearchTest {

    @Autowired
    private Environment environment;

    @BeforeEach
    void ensureTestDatabase() {
        String jdbcUrl = getPostgreSQLContainer().getJdbcUrl();
        if (!jdbcUrl.contains("test")) {
            throw new IllegalStateException("НЕБЕЗОПАСНО: тест запущен не в тестовой базе PostgreSQL!");
        }

        String testElasticUri = environment.getProperty("test.elasticsearch.uri");
        String actualUri = elasticsearchContainer.getHttpHostAddress();

        if (testElasticUri == null || !testElasticUri.equals(actualUri)) {
            throw new IllegalStateException("НЕБЕЗОПАСНО: тест запущен не с тестовым Elasticsearch контейнером!\n"
                    + "Expected: " + actualUri + ", but got: " + testElasticUri);
        }
    }

    private static Long lastCreatedUserId = 1L;
    private static Long lastCreatedTaskId = 1L;
    private static Long lastCreatedProjectId = 1L;
    private static Long lastCreatedCommentId = 1L;
    private final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.now();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TaskIndexRepository taskIndexRepository;

    @Autowired
    private ProjectIndexRepository projectIndexRepository;

    @Autowired
    private CommentIndexRepository commentIndexRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void clear() {
        taskIndexRepository.deleteAll();
        projectIndexRepository.deleteAll();
        commentIndexRepository.deleteAll();
        commentRepository.deleteAll();
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    public void deleteTestData() {
        clear();
    }

    @Test
    public void searchTaskIndexById_whenValidDate_thenGetTaskIndex() throws Exception {
        createNewTask();
        UUID taskId = UUID.fromString(UUID_Service.fromLong(lastCreatedTaskId).toString());
        mockMvc.perform(get("/search/tasks/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.status").value(TaskStatus.TODO))
                .andExpect(jsonPath("$.priority").value(TaskPriority.HIGH))
                .andExpect(jsonPath("$.deadline").value(LOCAL_DATE_TIME))
                .andExpect(jsonPath("$.assignedUserId").value(UUID_Service.fromLong(lastCreatedUserId).toString()))
                .andExpect(jsonPath("$.projectId").value(UUID_Service.fromLong(lastCreatedProjectId).toString()));
    }

    @Test
    public void searchTaskIndexes_whenStatusIsTODO_andPriorityIsHigh_thenReturnsExpectedTasks() throws Exception {
        for (int i = 0; i < 20; i++) {createNewTask();}
        mockMvc.perform(get("/search/tasks")
                        .param("status", "TODO")
                        .param("priority", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(UUID_Service.fromLong(lastCreatedTaskId).toString()))
                .andExpect(jsonPath("$.content[0].title").value("Title"))
                .andExpect(jsonPath("$.content[0].description").value("Description"))
                .andExpect(jsonPath("$.content[0].status").value("TODO"))
                .andExpect(jsonPath("$.content[0].priority").value("HIGH"))
                .andExpect(jsonPath("$.content[0].deadline").value(LOCAL_DATE_TIME.toString()))
                .andExpect(jsonPath("$.content[0].assignedUserId").value(UUID_Service.fromLong(lastCreatedUserId).toString()))
                .andExpect(jsonPath("$.content[0].projectId").value(UUID_Service.fromLong(lastCreatedProjectId).toString()))
                .andExpect(jsonPath("$.content", hasSize(20)));
    }

    @Test
    public void searchCommentIndexes_whenTaskIdIs_1_thenReturnsExpectedComment() throws Exception {
        for (int i = 0; i < 20; i++) {
            createNewComment();
        }
        mockMvc.perform(get("/search/comments")
                        .param("query", "Comment")
                        .param("taskId", UUID_Service.fromLong(lastCreatedTaskId).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(20)))
                .andExpect(jsonPath("$.content[0].comment").value("Comment"))
                .andExpect(jsonPath("$.content[0].taskId").value(UUID_Service.fromLong(lastCreatedTaskId).toString()))
                .andExpect(jsonPath("$.content[0].userId").value(UUID_Service.fromLong(lastCreatedUserId).toString()));
    }


    @Test
    public void searchProjectIndexesByNameOrDescription_whenValidDate_thenReturnsExpectedComment() throws Exception {
        for (int i = 0; i < 20; i++) {createNewProject();}
        mockMvc.perform(get("/search/projects")
                        .param("query", "Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Namedtf8"))
                .andExpect(jsonPath("$.content[0].description").value("gt97fygDescription"))
                .andExpect(jsonPath("$.content[0].status").value(ProjectStatus.ACTIVE.toString()))
                .andExpect(jsonPath("$.content", hasSize(20)));
    }


    private void createNewUser() {
        UserRequest userRequest = new UserRequest("FirstName", "LastName", getRandomEmail());
        User user = userService.saveUser(userRequest);
        lastCreatedUserId = user.getId();
    }

    private void createNewTask() {
        createNewUser();
        createNewProject();

        TaskRequest taskRequest = new TaskRequest(
                "Title",
                "Description",
                TaskStatus.TODO,
                TaskPriority.HIGH,
                null,
                lastCreatedUserId,
                lastCreatedProjectId
        );

        Task task = taskService.saveTask(taskRequest);
        lastCreatedTaskId = task.getId();
    }

    private void createNewProject() {
        ProjectRequest projectRequest = new ProjectRequest("Namedtf8", "gt97fygDescription", ProjectStatus.ACTIVE, UUID.randomUUID());
        Project project = projectService.saveProject(projectRequest);
        lastCreatedProjectId = project.getId();
    }

    private void createNewComment() {
        createNewTask();

        Comment comment = commentService.saveComment(
                new CommentCreateRequest("Comment", lastCreatedTaskId, lastCreatedUserId));
        lastCreatedCommentId = comment.getId();
    }

    public static String getRandomEmail() {
        String randomString = UUID.randomUUID().toString().substring(0, 16);
        return randomString + "@example.com";
    }
}
