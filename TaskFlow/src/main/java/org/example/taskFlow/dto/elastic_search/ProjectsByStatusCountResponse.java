package org.example.taskFlow.dto.elastic_search;

public record ProjectsByStatusCountResponse(
        String status,
        long count
) {}
