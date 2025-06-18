package org.example.taskFlow.unit.requestValidation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.taskFlow.dto.task.TaskRequest;
import org.example.taskFlow.enums.TaskPriority;
import org.example.taskFlow.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.Set;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskValidation {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @MethodSource("incorrectCommentRequestsCases")
    void testCommentValidation (TaskRequest request, String errorMessage) {
        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        String actualMessage = violations.iterator().next().getMessage();
        assertEquals(errorMessage, actualMessage);
    }

    static Stream<Arguments> incorrectCommentRequestsCases() {
        return Stream.of(
                Arguments.of(new TaskRequest("     ", "Description", TaskStatus.TODO, TaskPriority.LOW, null, 1L, 1L), "Заголовок не может быть пустым"),
                Arguments.of(new TaskRequest("Name", "        ", TaskStatus.TODO, TaskPriority.LOW, null, 1L, 1L), "Описание не может быть пустым"),
                Arguments.of(new TaskRequest("Name", "Description", null, TaskPriority.LOW, null, 1L, 1L), "Статус не может быть пустым"),
                Arguments.of(new TaskRequest("Name", "Description", TaskStatus.TODO, null, null, 1L, 1L), "Задача не может быть без приоритета"),
                Arguments.of(new TaskRequest("Name", "Description", TaskStatus.TODO, TaskPriority.LOW, null, null, 1L), "userId не должен быть пустым для задачи"),
                Arguments.of(new TaskRequest("Name", "Description", TaskStatus.TODO, TaskPriority.LOW, null, 1L, null), "Id проекта не может быть пустым")
        );
    }
}
