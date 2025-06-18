package org.example.taskFlow.dto.taskHistory;

import org.bson.types.ObjectId;
import org.example.taskFlow.model.TaskHistory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public record TaskHistoryResponse (
        ObjectId id,
        long taskId,
        String action,
        long performedBy,
        LocalDateTime timestamp,
        Map<String, Object> details
) {
    public static TaskHistoryResponse from (TaskHistory taskHistory) {
        return new TaskHistoryResponse(taskHistory.getId(), taskHistory.getTaskId(), taskHistory.getAction(), taskHistory.getPerformedBy(), taskHistory.getTimestamp(), (HashMap<String, Object>) taskHistory.getDetails());
    }
}
