package org.example.taskFlow.dto.taskHistory;

import java.util.Map;

public record TaskHistoryRequest (
        long taskId,
        String action,
        Long performedBy,
        Map<String, Object> details
) {}
