package org.example.taskFlow.unit.exception;

import org.example.taskFlow.component.FileProperties;
import org.example.taskFlow.exception.ErrorResponse;
import org.example.taskFlow.exception.GlobalExceptionHandler;
import org.example.taskFlow.exception.comment.CommentNotFoundException;
import org.example.taskFlow.exception.document.FileFormatNotSupportedException;
import org.example.taskFlow.exception.document.FileTooLargeException;
import org.example.taskFlow.exception.document.MongoDbDocumentNotFoundException;
import org.example.taskFlow.exception.project.ProjectNotFoundException;
import org.example.taskFlow.exception.task.TaskNotFoundException;
import org.example.taskFlow.exception.user.EmailAlreadyExistsException;
import org.example.taskFlow.exception.user.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExceptionTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private final FileProperties properties = new FileProperties();

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleNotFoundExceptions() {
        List<RuntimeException> exceptions = List.of(
                new TaskNotFoundException(1L),
                new UserNotFoundException(1L),
                new ProjectNotFoundException(1L),
                new CommentNotFoundException(1L),
                new MongoDbDocumentNotFoundException("1")
        );

        for (RuntimeException exception : exceptions) {
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFound(exception);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody().time());
        }

        ResponseEntity<ErrorResponse> userNotFoundResponse = globalExceptionHandler.handleNotFound(new UserNotFoundException(1L));
        ResponseEntity<ErrorResponse> projectNotFoundResponse = globalExceptionHandler.handleNotFound(new ProjectNotFoundException(1L));
        ResponseEntity<ErrorResponse> commentNotFoundResponse = globalExceptionHandler.handleNotFound(new CommentNotFoundException(1L));
        ResponseEntity<ErrorResponse> documentNotFoundResponse = globalExceptionHandler.handleNotFound(new MongoDbDocumentNotFoundException("1"));
        ResponseEntity<ErrorResponse> taskNotFoundResponse = globalExceptionHandler.handleNotFound(new TaskNotFoundException(1L));
        assertEquals("Пользователь с id: 1 не найден", userNotFoundResponse.getBody().message());
        assertEquals("Проект по id: 1 не найден", projectNotFoundResponse.getBody().message());
        assertEquals("Комментарий по этому Id: 1 не найден", commentNotFoundResponse.getBody().message());
        assertEquals("Документ по id: 1 не найден", documentNotFoundResponse.getBody().message());
        assertEquals("Задача с Id: 1 не найдена", taskNotFoundResponse.getBody().message());
    }

    @Test
    void handleConflictException () {
        EmailAlreadyExistsException emailAlreadyExistsException = new EmailAlreadyExistsException("javaTheBestLang@gmail.com");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConflictException(emailAlreadyExistsException);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody().time());
        assertEquals("Пользователь с email-ом javaTheBestLang@gmail.com уже существует", response.getBody().message());
    }

    @Test
    void handlePayloadToLargeException () {
        FileTooLargeException fileTooLargeException = new FileTooLargeException(10240, properties.getMaxSize());
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handlePayloadToLargeException(fileTooLargeException);
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertNotNull(response.getBody().time());
        assertEquals(String.format("Файл слишком большой (%d байт). Максимально допустимый размер: %s", 10240, properties.getMaxSize()), response.getBody().message());
    }

    @Test
    void handleUnsupportedMediaTypeException() {
        FileFormatNotSupportedException fileFormatNotSupportedException = new FileFormatNotSupportedException("GIF");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleUnsupportedMediaTypeException(fileFormatNotSupportedException);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        assertNotNull(response.getBody().time());
        assertEquals("Файл с форматом GIF не поддерживается", response.getBody().message());
    }
}
