package org.example.taskFlow.dto.elastic_search;

import java.util.UUID;

public record CommentsByUserCountResponse(
        UUID userId,
        long count
) {}
