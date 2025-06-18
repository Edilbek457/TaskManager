package org.example.taskFlow.service;

import lombok.RequiredArgsConstructor;
import org.example.taskFlow.dto.kafka.CommentLog;
import org.example.taskFlow.dto.comment.CommentCreateRequest;
import org.example.taskFlow.dto.comment.CommentUpdateRequest;
import org.example.taskFlow.enums.LogLevel;
import org.example.taskFlow.exception.comment.CommentNotFoundException;
import org.example.taskFlow.exception.task.TaskNotFoundException;
import org.example.taskFlow.exception.user.UserNotFoundException;
import org.example.taskFlow.model.Comment;
import org.example.taskFlow.model.Event;
import org.example.taskFlow.repository.CommentRepository;
import org.example.taskFlow.repository.TaskRepository;
import org.example.taskFlow.repository.UserRepository;
import org.example.taskFlow.service.elastic_search.CommentIndexService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskService taskService;
    private final LogService logEventService;
    private final EventService eventService;
    private final CommentIndexService commentIndexService;

    public Page<Comment> getAllCommentByTaskId (long taskId, Pageable pageable) {
        if (taskRepository.findById(taskId).isPresent()) {
            return commentRepository.findAllByTaskId(taskId, pageable);
        } else {
            throw new TaskNotFoundException(taskId);
        }
    }

    public Page<Comment> getAllCommentByTaskIdAndUserId (long taskId, long userId, Pageable pageable) {
        userAndTaskExists(taskId, userId);
        return commentRepository.findAllByTaskIdAndUserId(taskId, userId, pageable);
    }


    public Comment saveComment (CommentCreateRequest commentCreateRequest) {
        Comment comment = createComment(commentCreateRequest);
        Comment savedComment = commentRepository.save(comment);
        commentIndexService.saveCommentIndex(CommentIndexService.toCommentIndex(savedComment));
        logEventService.createAndSendLog(LogLevel.INFO, "Comment saved", CommentLog.toCommentLog(savedComment));
        Event createdEvent = eventService.createEvent("COMMENT_CREATED", UUID_Service.fromLong(comment.getId()), "COMMENT", null, comment);
        eventService.sentKafkaEvent(createdEvent);
        return savedComment;
    }

    public Comment createComment (CommentCreateRequest commentCreateRequest) {
        userAndTaskExists(commentCreateRequest);
        Comment comment = new Comment();
        comment.setContent(commentCreateRequest.content());
        comment.setUser(userService.getUserById(commentCreateRequest.userId()));
        comment.setTask(taskService.getTaskById(commentCreateRequest.taskId()));
        return comment;
    }

    public Comment updateComment (long id, CommentUpdateRequest commentUpdateRequest) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
        Comment oldComment = new Comment();
        oldComment.setContent(comment.getContent());
        comment.setContent(commentUpdateRequest.content());
        Comment updatedComment = commentRepository.save(comment);
        commentIndexService.updateCommentIndex(CommentIndexService.toCommentIndex(comment));
        logEventService.createAndSendLog(LogLevel.INFO, "Comment updated", CommentLog.toCommentLog(updatedComment));
        Event createdEven = eventService.createEvent("COMMENT_UPDATED", UUID_Service.fromLong(comment.getId()), "COMMENT", CommentUpdateRequest.from(oldComment), CommentUpdateRequest.from(updatedComment));
        eventService.sentKafkaEvent(createdEven);
        return updatedComment;
    }

    public void deleteComment (long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
        commentRepository.delete(comment);
        commentIndexService.deleteCommentIndex(UUID_Service.fromLong(comment.getId()));
        logEventService.createAndSendLog(LogLevel.INFO, "Comment deleted", CommentLog.toCommentLog(comment));
        Event createdEvent = eventService.createEvent("COMMENT_DELETED", UUID_Service.fromLong(comment.getId()), "COMMENT", comment, null);
        eventService.sentKafkaEvent(createdEvent);
    }

    public void userAndTaskExists (CommentCreateRequest commentCreateRequest) {
        long userId = commentCreateRequest.userId();
        long taskId = commentCreateRequest.taskId();
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        } else if (taskRepository.findById(taskId).isEmpty()) {
            throw new TaskNotFoundException(taskId);
        }
    }

    public void userAndTaskExists (long userId, long taskId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        } else if (taskRepository.findById(taskId).isEmpty()) {
            throw new TaskNotFoundException(taskId);
        }
    }
}
