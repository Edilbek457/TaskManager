package org.example.taskFlow.dto.user_security;

public record JwtAccessToken (
     String userId,
     String email,
     String session
){}
