package org.example.taskFlow.controller.elastic_search;

import lombok.RequiredArgsConstructor;
import org.example.taskFlow.dto.elastic_search.CommentsByUserCountResponse;
import org.example.taskFlow.dto.elastic_search.ProjectsByStatusCountResponse;
import org.example.taskFlow.dto.elastic_search.TasksByPriorityCountResponse;
import org.example.taskFlow.dto.elastic_search.TasksByStatusCountResponse;
import org.example.taskFlow.service.elastic_search.AnalyticService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticController {
    private final AnalyticService analyticService;

    @GetMapping("/tasks/status")
    public ResponseEntity<List<TasksByStatusCountResponse>> tasksByStatus() {
        return ResponseEntity.status(HttpStatus.OK).body(analyticService.countTasksByStatus().entrySet().stream()
                .map(e -> new TasksByStatusCountResponse(e.getKey(), e.getValue())).toList());
    }

    @GetMapping("/tasks/priority")
    public ResponseEntity<List<TasksByPriorityCountResponse>> tasksByPriority() {
        return ResponseEntity.status(HttpStatus.OK).body(analyticService.countTasksByPriority().entrySet().stream()
                .map(e -> new TasksByPriorityCountResponse(e.getKey(), e.getValue())).toList());
    }

    @GetMapping("/comments/users")
    public ResponseEntity<List<CommentsByUserCountResponse>> commentsByUser() {
        return ResponseEntity.ok(analyticService.countCommentsByUser().entrySet().stream()
                .map(e -> new CommentsByUserCountResponse(e.getKey(), e.getValue())).toList());
    }

    @GetMapping("/projects/status")
    public ResponseEntity<List<ProjectsByStatusCountResponse>> projectsByStatus() {
        return ResponseEntity.ok(analyticService.countProjectsByStatus().entrySet().stream()
                .map(e -> new ProjectsByStatusCountResponse(e.getKey(), e.getValue())).toList());
    }
}
