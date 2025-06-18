package org.example.taskFlow.service.payload;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Objects;

public interface PayloadFactory {
    String createPayload(Object firstObject, Object secondObject);

    static void changeChecker(StringBuilder sb, Field field, Object obj1, Object obj2) {
        try {
            MethodHandle getter = MethodHandles.publicLookup().findVirtual(
                    obj1.getClass(),
                    field.getName(),
                    MethodType.methodType(field.getType())
            );

            Object value1 = getter.invoke(obj1);
            Object value2 = getter.invoke(obj2);

            if (!Objects.equals(value1, value2)) {
                sb.append("Поле \"")
                        .append(field.getName())
                        .append("\" было изменено: \"")
                        .append(value1)
                        .append("\" -> \"")
                        .append(value2)
                        .append("\"\n");
            }
        } catch (Throwable e) {
            throw new RuntimeException("Ошибка при доступе к полю: " + field.getName(), e);
        }
    }
}

