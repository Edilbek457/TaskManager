package org.example.taskFlow.service.elastic_search;

import lombok.RequiredArgsConstructor;
import org.example.taskFlow.exception.elastic_search.ElasticSearchDocumentNotFoundException;
import org.example.taskFlow.model.Comment;
import org.example.taskFlow.model.elastic_search.CommentIndex;
import org.example.taskFlow.repository.elastic_search.CommentIndexRepository;
import org.example.taskFlow.service.UUID_Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentIndexService {

    private final CommentIndexRepository commentIndexRepository;

    public Page<CommentIndex> searchComments(
            String query, UUID taskId, Pageable pageable) {
        if (query != null && !query.isBlank()) {
            return commentIndexRepository.searchByCommentAndTaskId(query, taskId, pageable);
        } else if (taskId != null) {
            return commentIndexRepository.searchAllByTaskId(taskId, pageable);
        } return commentIndexRepository.findAll(pageable);
    }

    public static CommentIndex toCommentIndex(Comment comment) {
        CommentIndex commentIndex = new CommentIndex();
        commentIndex.setId(UUID_Service.fromLong(comment.getId()));
        commentIndex.setComment(comment.getContent());
        commentIndex.setTaskId(UUID_Service.fromLong(comment.getTask().getId()));
        commentIndex.setUserId(UUID_Service.fromLong(comment.getUser().getId()));
        commentIndex.setCreatedAt(comment.getCreatedAt());
        return commentIndex;
    }

    public CommentIndex getCommentIndex(UUID commentIndexId) {
        return commentIndexRepository.findById(commentIndexId).orElseThrow(() -> new ElasticSearchDocumentNotFoundException(commentIndexId));
    }

    public void saveCommentIndex(CommentIndex commentIndex) {
        commentIndexRepository.save(commentIndex);
    }

    public void updateCommentIndex(CommentIndex commentIndex) {
        CommentIndex updatedCommentIndex = getCommentIndex(commentIndex.getId());
        updatedCommentIndex.setComment(commentIndex.getComment());
        commentIndexRepository.save(updatedCommentIndex);
    }

    public void deleteCommentIndex(UUID id) {
        commentIndexRepository.deleteById(id);
    }
}
