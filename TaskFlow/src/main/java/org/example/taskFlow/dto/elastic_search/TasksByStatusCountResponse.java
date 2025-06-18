package org.example.taskFlow.dto.elastic_search;

public record TasksByStatusCountResponse(
        String status,
        long count
) {}