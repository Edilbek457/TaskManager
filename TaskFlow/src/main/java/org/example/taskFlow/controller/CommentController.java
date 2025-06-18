package org.example.taskFlow.controller;

import lombok.RequiredArgsConstructor;
import org.example.taskFlow.dto.comment.CommentCreateRequest;
import org.example.taskFlow.dto.comment.CommentResponse;
import org.example.taskFlow.dto.comment.CommentUpdateRequest;
import org.example.taskFlow.model.Comment;
import org.example.taskFlow.service.CommentService;
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
@RequestMapping("/tasks/comment/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<Page<CommentResponse>> getAllCommentByTaskId(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @RequestParam(defaultValue = "id") String sortBy,
                                                                       @RequestParam(defaultValue = "asc") String direction,
                                                                       @PathVariable long taskId
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Comment> commentPage = commentService.getAllCommentByTaskId(taskId, pageable);
        Page<CommentResponse> commentResponses = commentPage.map(CommentResponse::from);
        return ResponseEntity.status(HttpStatus.OK).body(commentResponses);
    }

    @GetMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<Page<CommentResponse>> getAllCommentByUserId(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @RequestParam(defaultValue = "id") String sortBy,
                                                                       @RequestParam(defaultValue = "asc") String direction,
                                                                       @PathVariable long taskId,
                                                                       @PathVariable long commentId
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Comment> commentPage = commentService.getAllCommentByTaskIdAndUserId(taskId, commentId, pageable);
        Page<CommentResponse> commentResponses = commentPage.map(CommentResponse::from);
        return ResponseEntity.status(HttpStatus.OK).body(commentResponses);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<CommentResponse> createComment (@RequestBody CommentCreateRequest commentCreateRequest) {
        Comment comment = commentService.saveComment(commentCreateRequest);
        CommentResponse commentResponse = CommentResponse.from(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<CommentResponse> updateComment (@RequestParam long commentId, @RequestBody CommentUpdateRequest commentUpdateRequest) {
        Comment comment = commentService.updateComment(commentId, commentUpdateRequest);
        CommentResponse commentResponse = CommentResponse.from(comment);
        return ResponseEntity.status(HttpStatus.OK).body(commentResponse);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<CommentResponse> deleteComment (@RequestParam long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
