package org.example.taskFlow.dto.user_security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.taskFlow.validation.interfaces.PasswordStrength;

public record PasswordUpdateRequest(

        @PasswordStrength
        @NotBlank(message = "Пароль не может быть пустым или содержать пробелов")
        @Size(min = 8, message = "Пароль должен быть минимум из 8 символов и быть достаточно сложным (Содержать спецсимволы и цифры)")
        String firstPassword,

        @PasswordStrength
        @NotBlank(message = "Пароль не может быть пустым или содержать пробелов")
        @Size(min = 8, message = "Пароль должен быть минимум из 8 символов и быть достаточно сложным (Содержать спецсимволы и цифры)")
        String secondPassword

) {

}
