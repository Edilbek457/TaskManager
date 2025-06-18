package org.example.taskFlow.dto.user_security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest (

        @Email(message = "Неправильный формат email или неверное значение")
        String email,

        @NotBlank(message = "Пароль не может быть пустым или содержать пробелов")
        @Size(min = 8, message = "Пароль должен быть минимум из 8 символов и быть достаточно сложным (Содержать спецсимволы и цифры)")
        String password

) {}

