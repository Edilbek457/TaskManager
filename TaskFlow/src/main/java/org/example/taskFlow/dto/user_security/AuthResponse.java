package org.example.taskFlow.dto.user_security;

public record AuthResponse (

        String accessToken,
        String refreshToken

) {}
