package org.example.taskFlow.dto.user_security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.taskFlow.validation.interfaces.PasswordStrength;

public record UserCreateRequest (

        @NotBlank(message = "Имя не должно быть пустым")
        @Size(max = 64, message = "Имя не должно превышать 64 символа")
        String firstName,

        @NotBlank(message = "Фамилия не должна быть пустой")
        @Size(max = 64, message = "Фамилия не должна превышать 64 символа")
        String lastName,

        @Email(message = "Неправильный формат email или неверное значение")
        String email,

        @PasswordStrength
        @NotBlank(message = "Пароль не может быть пустым или содержать пробелов")
        @Size(min = 8, message = "Пароль должен быть минимум из 8 символов и быть достаточно сложным (Содержать спецсимволы и цифры)")
        String password
) {}