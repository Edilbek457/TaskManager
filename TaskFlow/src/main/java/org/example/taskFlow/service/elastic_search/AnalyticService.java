package org.example.taskFlow.service.elastic_search;

import lombok.RequiredArgsConstructor;
import org.example.taskFlow.enums.ProjectStatus;
import org.example.taskFlow.enums.TaskPriority;
import org.example.taskFlow.enums.TaskStatus;
import org.example.taskFlow.model.elastic_search.CommentIndex;
import org.example.taskFlow.repository.elastic_search.CommentIndexRepository;
import org.example.taskFlow.repository.elastic_search.ProjectIndexRepository;
import org.example.taskFlow.repository.elastic_search.TaskIndexRepository;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AnalyticService {

    private final TaskIndexRepository taskIndexRepository;
    private final CommentIndexRepository commentIndexRepository;
    private final ProjectIndexRepository projectIndexRepository;

    public Map<String, Long> countTasksByStatus() {
        return Arrays.stream(TaskStatus.values())
                .collect(Collectors.toMap(
                        TaskStatus::name,
                        taskIndexRepository::countByStatus
                ));
    }

    public Map<String, Long> countTasksByPriority() {
        return Arrays.stream(TaskPriority.values())
                .collect(Collectors.toMap(
                        TaskPriority::name,
                        taskIndexRepository::countByPriority
                ));
    }

    public Map<UUID, Long> countCommentsByUser() {
        Iterable<CommentIndex> all = commentIndexRepository.findAll();
        List<UUID> users = StreamSupport.stream(all.spliterator(), false)
                .map(CommentIndex::getUserId)
                .distinct()
                .toList();
        return users.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        commentIndexRepository::countByUserId
                ));

    }

    public Map<String, Long> countProjectsByStatus() {
        return Arrays.stream(ProjectStatus.values())
                .collect(Collectors.toMap(
                        ProjectStatus::name,
                        projectIndexRepository::countByStatus
                ));
    }
}
