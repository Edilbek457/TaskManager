package org.example.consumer.model;

import java.util.Map;

public class ErrorStatusMap {
    public static final Map<Integer, String> errorStatusMap = Map.of(
            100, "Ошибка с базой данных",
            200, "Ошибка с парсингом или Json",
            300, "Ошибка с валидацией",
            400, "Проверка DLQ"
    );

    public static String getMessage(int code) {
        return errorStatusMap.getOrDefault(code, "Неизвестная ошибка");
    }
}
