package org.example.taskFlow.controller.elastic_search;

import lombok.RequiredArgsConstructor;
import org.example.taskFlow.model.elastic_search.CommentIndex;
import org.example.taskFlow.model.elastic_search.ProjectIndex;
import org.example.taskFlow.model.elastic_search.TaskIndex;
import org.example.taskFlow.service.elastic_search.CommentIndexService;
import org.example.taskFlow.service.elastic_search.ProjectIndexService;
import org.example.taskFlow.service.elastic_search.TaskIndexService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final TaskIndexService taskIndexService;
    private final CommentIndexService commentIndexService;
    private final ProjectIndexService projectIndexService;

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskIndex> searchTask(
            @PathVariable UUID taskId) {
        return ResponseEntity.status(HttpStatus.OK).body(taskIndexService.getTaskIndexById(taskId));
    }

    @GetMapping("/tasks")
    public ResponseEntity<Page<TaskIndex>> searchTasks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        Pageable pageable = Pageable.unpaged();
        return ResponseEntity.status(HttpStatus.OK).body(taskIndexService.searchTasks(query, status, priority, pageable));
    }

    @GetMapping("/comments")
    public ResponseEntity<Page<CommentIndex>> searchComments(
            @RequestParam String query,
            @RequestParam UUID taskId) {
        Pageable pageable = Pageable.unpaged();
        return ResponseEntity.status(HttpStatus.OK).body(commentIndexService.searchComments(query, taskId, pageable));
    }

    @GetMapping("/projects")
    public ResponseEntity<Page<ProjectIndex>> searchProjects(
            @RequestParam String query) {
        Pageable pageable = Pageable.unpaged();
        return ResponseEntity.status(HttpStatus.OK).body(projectIndexService.searchProjects(query, pageable));
    }
}
