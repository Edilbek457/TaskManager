package org.example.taskFlow.controller.oldController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.taskFlow.dto.task.TaskRequest;
import org.example.taskFlow.dto.task.TaskResponse;
import org.example.taskFlow.model.Task;
import org.example.taskFlow.service.TaskService;
import org.example.taskFlow.service.security.JwtService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final JwtService jwtService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<Page<TaskResponse>> getAllTasks(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "id") String sortBy,
                                                          @RequestParam(defaultValue = "asc") String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task> taskPage = taskService.getAllTasks(pageable);
        Page<TaskResponse> taskResponses = taskPage.map(TaskResponse::from);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable long id) {
        Task task = taskService.getTaskById(id);
        TaskResponse taskResponse = TaskResponse.from(task);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponse);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<TaskResponse> createTask(@RequestBody @Valid TaskRequest taskRequest) {
        Task task = taskService.saveTask(taskRequest);
        TaskResponse taskResponse = TaskResponse.from(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<TaskResponse> updateTaskById(@RequestBody @Valid TaskRequest taskRequest, @PathVariable long id) {
        Task task = taskService.updateTask(id, taskRequest);
        TaskResponse taskResponse = TaskResponse.from(task);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<Void> deleteTaskById(@PathVariable long id) {
        taskService.deleteTask(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
