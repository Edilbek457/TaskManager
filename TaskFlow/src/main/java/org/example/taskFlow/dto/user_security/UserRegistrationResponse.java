package org.example.taskFlow.dto.user_security;

import org.example.taskFlow.model.User;

public record UserRegistrationResponse(
   AuthResponse authResponse,
   User user
) {}
