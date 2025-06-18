package org.example.taskFlow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.taskFlow.validation.interfaces.PasswordStrength;

public class PasswordStrengthValidator implements ConstraintValidator<PasswordStrength, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) return false;

        int strengthScore = 0;
        if (password.length() >= 8) strengthScore++;
        if (password.length() >= 16) strengthScore+=2;
        if (password.length() >= 32) strengthScore+=4;
        if (password.length() >= 64) strengthScore+=7;
        if (password.matches(".*[A-Z].*")) strengthScore++;
        if (password.matches(".*[a-z].*")) strengthScore++;
        if (password.matches(".*[А-ЯЁ].*"))  strengthScore++;
        if (password.matches(".*[a-яё].*")) strengthScore++;
        if (password.matches(".*\\d.*")) strengthScore++;
        if (password.matches(".*[*@#$%^&+=?!].*")) strengthScore+=2;
        if (password.matches(".*[()\\-_{}\\[\\]:;\"'|<>,./~`].*")) strengthScore+=2;
        return strengthScore >= 5;
    }
}

