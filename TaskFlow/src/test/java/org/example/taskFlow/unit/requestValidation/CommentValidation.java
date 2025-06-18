package org.example.taskFlow.unit.requestValidation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.taskFlow.dto.comment.CommentCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.Set;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentValidation  {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @MethodSource("incorrectCommentRequestsCases")
    void testCommentValidation (CommentCreateRequest request, String errorMessage) {
        Set<ConstraintViolation<CommentCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        String actualMessage = violations.iterator().next().getMessage();
        assertEquals(errorMessage, actualMessage);
    }

    static Stream<Arguments> incorrectCommentRequestsCases() {
        return Stream.of(
                Arguments.of(new CommentCreateRequest("A".repeat(2000), 1L, 1L), "Комментарий слишком длинный"),
                Arguments.of(new CommentCreateRequest("   ", 1L, 1L), "Комментарий не должен быть пустым"),
                Arguments.of(new CommentCreateRequest("Comment", null, 1L), "Комментарий должен быть прикреплен к задаче"),
                Arguments.of(new CommentCreateRequest("Comment", 1L, null), "Неопределенный пользователь")
        );
    }
}
