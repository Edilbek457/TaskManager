package org.example.taskFlow.dto.user_security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.taskFlow.model.User;

public record UserUpdateRequest (

        @NotBlank(message = "Имя не должно быть пустым")
        @Size(max = 64, message = "Имя не должно превышать 64 символа")
        String firstName,

        @NotBlank(message = "Фамилия не должна быть пустой")
        @Size(max = 64, message = "Фамилия не должна превышать 64 символа")
        String lastName

) {
        public UserUpdateRequest from (User use) {
           return new UserUpdateRequest(use.getFirstName(), use.getLastName());
        }
}
