package org.example.taskFlow.unit.requestValidation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.taskFlow.dto.project.ProjectRequest;
import org.example.taskFlow.enums.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectValidation {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @MethodSource("incorrectCommentRequestsCases")
    void testCommentValidation (ProjectRequest request, String errorMessage) {
        Set<ConstraintViolation<ProjectRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        String actualMessage = violations.iterator().next().getMessage();
        assertEquals(errorMessage, actualMessage);
    }

    static Stream<Arguments> incorrectCommentRequestsCases() {
        return Stream.of(
                Arguments.of(new ProjectRequest("      ", "Description", ProjectStatus.ARCHIVED, UUID.randomUUID()), "Название проекта не должно быть пустым"),
                Arguments.of(new ProjectRequest("Name".repeat(20), "Description", ProjectStatus.ARCHIVED, UUID.randomUUID()), "Название проекта не должно превышать 64 символа"),
                Arguments.of(new ProjectRequest("Name",  "       ", ProjectStatus.ARCHIVED, UUID.randomUUID()), "Описание проекта не может быть пустым"),
                Arguments.of(new ProjectRequest("Name",  "Description", null, UUID.randomUUID()), "Статус проекта не указан")
        );
    }
}
