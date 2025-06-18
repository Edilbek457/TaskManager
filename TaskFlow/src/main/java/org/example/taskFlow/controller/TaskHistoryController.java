package org.example.taskFlow.controller;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.taskFlow.dto.taskHistory.TaskHistoryResponse;
import org.example.taskFlow.model.TaskHistory;
import org.example.taskFlow.service.TaskHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskHistoryController {

    private final TaskHistoryService taskHistoryService;

    @GetMapping("/{taskId}/history")
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<List<TaskHistoryResponse>> getTaskHistory (ObjectId objectId) {
        List<TaskHistory> taskHistoryList = taskHistoryService.getTaskHistoryListByTaskId(objectId);
        List<TaskHistoryResponse> taskHistoryResponseList = taskHistoryList.stream()
                .map(TaskHistoryResponse::from)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(taskHistoryResponseList);
    }
}
