package org.example.taskFlow.service;

import java.util.UUID;

public class UUID_Service {
    public static UUID fromLong(Long id) {
        return new UUID(0L, id);
    }
    public static Long toLong(UUID uuid) {
        return uuid.getLeastSignificantBits();
    }
}

