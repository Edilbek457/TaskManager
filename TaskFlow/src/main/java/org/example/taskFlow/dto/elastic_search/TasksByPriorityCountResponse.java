package org.example.taskFlow.dto.elastic_search;

public record TasksByPriorityCountResponse(
        String status,
        long count
) {}
