package org.example.taskFlow.unit.requestValidation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.taskFlow.dto.user.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.Set;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserValidation {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @MethodSource("incorrectCommentRequestsCases")
    void testCommentValidation (UserRequest request, String errorMessage) {
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        String actualMessage = violations.iterator().next().getMessage();
        assertEquals(errorMessage, actualMessage);
    }

    static Stream<Arguments> incorrectCommentRequestsCases() {
        return Stream.of(
                Arguments.of(new UserRequest("     ", "LastName", "JavaTheBestLang@gmail.com"), "Имя не должно быть пустым"),
                Arguments.of(new UserRequest("FirstName".repeat(20), "LastName", "JavaTheBestLang@gmail.com"), "Имя не должно превышать 64 символа"),
                Arguments.of(new UserRequest("FirstName", "       ", "JavaTheBestLang@gmail.com"), "Фамилия не должна быть пустой"),
                Arguments.of(new UserRequest("FirstName", "LastName".repeat(20), "JavaTheBestLang@gmail.com"), "Фамилия не должна превышать 64 символа"),
                Arguments.of(new UserRequest("FirstName", "LastName", "JavaTheBestLang"), "Неправильный формат email или неверное значение")
        );
    }
}
