package org.example.taskFlow.repository;

import org.example.taskFlow.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByTaskId(long taskId, Pageable pageable);
    Page<Comment> findAllByTaskIdAndUserId(long taskId, long userId, Pageable pageable);
}
