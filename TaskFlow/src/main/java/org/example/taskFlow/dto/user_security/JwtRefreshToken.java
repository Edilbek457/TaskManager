package org.example.taskFlow.dto.user_security;

public record JwtRefreshToken (
        String userId,
        String role,
        String email,
        String session
){}
