package org.example.taskFlow.service.payload;

import org.example.taskFlow.dto.task.TaskRequest;
import java.lang.reflect.Field;

public class TaskPayloadFactory implements PayloadFactory {
    @Override
    public String createPayload(Object firstObject, Object secondObject) {
        if (!(firstObject instanceof TaskRequest t1) || !(secondObject instanceof TaskRequest t2)) {
            throw new IllegalArgumentException("Ожидались объекты типа TaskRequest");
        }

        StringBuilder sb = new StringBuilder("{");
        for (Field field : TaskRequest.class.getDeclaredFields()) {
            PayloadFactory.changeChecker(sb, field, t1, t2);
        }
        return sb.append("}").toString();
    }
}



