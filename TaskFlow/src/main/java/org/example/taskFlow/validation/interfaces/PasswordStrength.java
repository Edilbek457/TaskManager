package org.example.taskFlow.validation.interfaces;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.taskFlow.validation.PasswordStrengthValidator;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordStrengthValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordStrength {
    String message() default "Очень слабый пароль. Попробуйте увеличить размер пароля или добавить спец символы";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

