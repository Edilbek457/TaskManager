package org.example.taskFlow.repository.elastic_search;

import org.example.taskFlow.model.elastic_search.CommentIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentIndexRepository extends ElasticsearchRepository<CommentIndex, UUID> {
    @Query("""
                {
                  "bool": {
                    "must": [
                      { "match": { "comment": "#{#query}" }},
                      { "term": { "taskId": "#{#taskId}" }}
                    ]
                  }
                }
            """)
    Page<CommentIndex> searchByCommentAndTaskId(@Param("query") String query, @Param("taskId") UUID taskId, Pageable pageable);


    long countByUserId(UUID userId);


    @Query("""
                {
                  "term": {
                    "taskId": "#{#taskId}"
                  }
                }
            """)
    Page<CommentIndex> searchAllByTaskId(@Param("taskId") UUID taskId, Pageable pageable);
}
