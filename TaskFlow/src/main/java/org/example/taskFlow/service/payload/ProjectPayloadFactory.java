package org.example.taskFlow.service.payload;

import org.example.taskFlow.dto.project.ProjectRequest;
import java.lang.reflect.Field;

public class ProjectPayloadFactory implements PayloadFactory {
    @Override
    public String createPayload(Object firstObject, Object secondObject) {
        if (!(firstObject instanceof ProjectRequest p1) || !(secondObject instanceof ProjectRequest p2)) {
            throw new IllegalArgumentException("Ожидались объекты типа ProjectRequest");
        }

        StringBuilder sb = new StringBuilder("{");
        for (Field field : ProjectRequest.class.getDeclaredFields()) {
            PayloadFactory.changeChecker(sb, field, p1, p2);
        }
        return sb.append("}").toString();
    }
}


