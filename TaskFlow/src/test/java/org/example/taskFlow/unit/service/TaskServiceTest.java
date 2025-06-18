package org.example.taskFlow.unit.service;

import org.example.taskFlow.dto.task.TaskRequest;
import org.example.taskFlow.enums.TaskPriority;
import org.example.taskFlow.enums.TaskStatus;
import org.example.taskFlow.exception.project.ProjectNotFoundException;
import org.example.taskFlow.exception.task.TaskNotFoundException;
import org.example.taskFlow.exception.user.UserNotFoundException;
import org.example.taskFlow.model.Project;
import org.example.taskFlow.model.Task;
import org.example.taskFlow.model.User;
import org.example.taskFlow.repository.TaskRepository;
import org.example.taskFlow.service.ProjectService;
import org.example.taskFlow.service.TaskHistoryService;
import org.example.taskFlow.service.TaskService;
import org.example.taskFlow.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    private final static long TASK_ID = 1L;
    private final static LocalDateTime TASK_DEADLINE = LocalDateTime.now().plusDays(1);

    @Mock
    private UserService userService;

    @Mock
    private ProjectService projectService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskHistoryService taskHistoryService;

    @InjectMocks
    private TaskService taskService;

    @Test
    public void getAllTasks_whenTasksCount_20_thenGet_20_tasks() {
        Pageable pageable = PageRequest.of(0, 20);

        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Task task = new Task();
            task.setId(TASK_ID);
            tasks.add(task);
        }
        Page<Task> mockPage = new PageImpl<>(tasks, pageable, tasks.size());
        when(taskRepository.findAll(pageable)).thenReturn(mockPage);
        Page<Task> result = taskService.getAllTasks(pageable);

        assertNotNull(result);
        assertEquals(20, result.getContent().size());
        verify(taskRepository).findAll(pageable);
    }

    @Test
    public void getTaskById_whenTaskExists_thenGetTask() {
        Task task = new Task();
        task.setId(TASK_ID);

        when(taskRepository.findTaskById(TASK_ID)).thenReturn(Optional.of(task));
        Task result = taskService.getTaskById(TASK_ID);

        assertNotNull(result);
        assertEquals(TASK_ID, result.getId());
    }

    @Test
    public void getTaskById_whenTaskNotExists_thenGetTask() {
        when(taskRepository.findTaskById(TASK_ID)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(TASK_ID));
        verify(taskRepository).findTaskById(TASK_ID);
    }

    @Test
    public void saveTask_whenTaskValidData_thenSaveTask() {
        TaskRequest taskRequest = new TaskRequest("Title", "Description", TaskStatus.TODO, TaskPriority.LOW, TASK_DEADLINE, 1L, 1L);
        User user = new User();
        user.setId(taskRequest.userId());
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setEmail("email@gmail.com");
        user.setActive(true);
        Project project = new Project();
        project.setId(taskRequest.projectId());
        project.setName("ProjectName");
        project.setDescription("Description");
        project.setOwnerId(UUID.randomUUID());
        when(userService.getUserById(taskRequest.userId())).thenReturn(user);
        when(projectService.getProjectById(taskRequest.projectId())).thenReturn(project);
        Task savedTask = new Task();
        savedTask.setId(TASK_ID);
        savedTask.setTitle(taskRequest.title());
        savedTask.setDescription(taskRequest.description());
        savedTask.setStatus(taskRequest.status());
        savedTask.setTaskPriority(taskRequest.priority());
        savedTask.setDeadline(taskRequest.deadline());
        savedTask.setUser(user);
        savedTask.setProject(project);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        Task result = taskService.saveTask(taskRequest);

        assertNotNull(result);
        assertNotNull(result.getUser());
        assertNotNull(result.getProject());
        assertEquals(TASK_ID, result.getId());
        assertEquals(taskRequest.title(), result.getTitle());
        assertEquals(taskRequest.description(), result.getDescription());
        assertEquals(taskRequest.deadline(), result.getDeadline());
        assertEquals(taskRequest.status(), result.getStatus());
        assertEquals(taskRequest.priority(), result.getTaskPriority());
        assertEquals(taskRequest.userId(), result.getUser().getId());
        assertEquals(taskRequest.projectId(), result.getProject().getId());

        verify(userService).getUserById(taskRequest.userId());
        verify(projectService).getProjectById(taskRequest.projectId());
        verify(taskRepository).save(any(Task.class));
        verify(taskHistoryService).saveTaskHistory(TASK_ID, "CREATE", -1L, Map.of());
    }

    @Test
    public void saveTask_whenUserNotExist_thenThrowException() {
        TaskRequest taskRequest = new TaskRequest("Title", "Description", TaskStatus.TODO, TaskPriority.LOW, TASK_DEADLINE, 1L, 1L);
        when(userService.getUserById(taskRequest.userId())).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> taskService.saveTask(taskRequest));
        verify(userService).getUserById(taskRequest.userId());
    }

    @Test
    public void saveTask_whenProjectNotExist_thenThrowException() {
        TaskRequest taskRequest = new TaskRequest("Title", "Description", TaskStatus.TODO, TaskPriority.LOW, TASK_DEADLINE, 1L, 1L);

        when(userService.getUserById(taskRequest.userId())).thenReturn(new User());
        when(projectService.getProjectById(taskRequest.projectId())).thenThrow(ProjectNotFoundException.class);

        assertThrows(ProjectNotFoundException.class, () -> taskService.saveTask(taskRequest));
        verify(userService).getUserById(taskRequest.userId());
        verify(projectService).getProjectById(taskRequest.projectId());
    }

    @Test
    public void updateTask_whenTaskExists_thenUpdateTask() {
        TaskRequest taskRequest = new TaskRequest("Title", "Description", TaskStatus.TODO, TaskPriority.LOW, TASK_DEADLINE, 1L, 1L);
        Task existingTask = new Task();
        existingTask.setId(TASK_ID);
        existingTask.setTitle("NoTitle");
        existingTask.setDescription("NoDescription");
        existingTask.setStatus(TaskStatus.DONE);
        existingTask.setTaskPriority(TaskPriority.HIGH);
        existingTask.setDeadline(null);
        existingTask.setUser(new User());
        existingTask.setProject(new Project());
        User user = new User();
        user.setId(taskRequest.userId());
        Project project = new Project();
        project.setId(taskRequest.projectId());
        Task savedTask = new Task();
        savedTask.setId(TASK_ID);
        savedTask.setTitle(taskRequest.title());
        savedTask.setDescription(taskRequest.description());
        savedTask.setStatus(taskRequest.status());
        savedTask.setTaskPriority(taskRequest.priority());
        savedTask.setDeadline(taskRequest.deadline());
        savedTask.setUser(user);
        savedTask.setProject(project);

        when(taskRepository.findTaskById(TASK_ID)).thenReturn(Optional.of(existingTask));
        when(userService.getUserById(taskRequest.userId())).thenReturn(user);
        when(projectService.getProjectById(taskRequest.projectId())).thenReturn(project);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        Task result = taskService.updateTask(TASK_ID, taskRequest);

        assertNotNull(result);
        assertEquals(TASK_ID, result.getId());
        assertEquals(taskRequest.title(), result.getTitle());
        assertEquals(taskRequest.description(), result.getDescription());
        assertEquals(taskRequest.deadline(), result.getDeadline());
        assertEquals(taskRequest.status(), result.getStatus());
        assertEquals(taskRequest.priority(), result.getTaskPriority());
        assertEquals(taskRequest.userId(), result.getUser().getId());
        assertEquals(taskRequest.projectId(), result.getProject().getId());

        verify(taskRepository).findTaskById(TASK_ID);
        verify(userService).getUserById(taskRequest.userId());
        verify(projectService).getProjectById(taskRequest.projectId());
        verify(taskRepository).save(any(Task.class));
        verify(taskHistoryService).saveTaskHistory(TASK_ID,  "UPDATE", -1L, Map.of());
    }

    @Test
    public void updateTask_whenTaskNotExists_thenThrowException() {
        TaskRequest taskRequest = new TaskRequest("Title", "Description", TaskStatus.TODO, TaskPriority.LOW, TASK_DEADLINE, 1L, 1L);

        when(taskRepository.findTaskById(TASK_ID)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(TASK_ID, taskRequest));
        verify(taskRepository).findTaskById(taskRequest.userId());
    }

    @Test
    public void deleteTask_whenTaskExists_thenDeleteTask() {
        Task task = new Task();
        task.setId(TASK_ID);
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));

        taskService.deleteTask(TASK_ID);

        verify(taskRepository).findById(TASK_ID);
        verify(taskRepository).deleteById(TASK_ID);
        verify(taskHistoryService).saveTaskHistory(TASK_ID, "DELETE", -1L, Map.of());
    }

    @Test
    public void deleteTask_whenTaskNotExists_thenThrowException() {
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(TASK_ID));

        verify(taskRepository).findById(TASK_ID);
        verify(taskRepository, never()).deleteById(anyLong());
    }


    @Test
    public void createTask_thenCreateTask() {
        TaskRequest taskRequest = new TaskRequest("Title", "Description", TaskStatus.TODO, TaskPriority.LOW, TASK_DEADLINE, 1L, 1L);

        Task task = taskService.createTask(taskRequest);

        assertNotNull(task);
        assertEquals(taskRequest.title(), task.getTitle());
        assertEquals(taskRequest.description(), task.getDescription());
        assertEquals(taskRequest.deadline(), task.getDeadline());
        assertEquals(taskRequest.status(), task.getStatus());
        assertEquals(taskRequest.priority(), task.getTaskPriority());
    }
}
