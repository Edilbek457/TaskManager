package org.example.taskFlow.service.payload;

import org.example.taskFlow.dto.user_security.UserUpdateRequest;

import java.lang.reflect.Field;

public class UserPayloadFactory implements PayloadFactory {
    @Override
    public String createPayload(Object firstObject, Object secondObject) {
        if (!(firstObject instanceof UserUpdateRequest u1) || !(secondObject instanceof UserUpdateRequest u2)) {
            throw new IllegalArgumentException("Ожидались объекты типа UserRequest");
        }

        StringBuilder sb = new StringBuilder("{");
        for (Field field : UserUpdateRequest.class.getDeclaredFields()) {
            PayloadFactory.changeChecker(sb, field, u1, u2);
        }
        return sb.append("}").toString();
    }
}

