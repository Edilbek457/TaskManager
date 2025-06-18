package org.example.taskFlow.exception;

import org.example.taskFlow.exception.comment.CommentNotFoundException;
import org.example.taskFlow.exception.document.MongoDbDocumentNotFoundException;
import org.example.taskFlow.exception.document.FileFormatNotSupportedException;
import org.example.taskFlow.exception.document.FileTooLargeException;
import org.example.taskFlow.exception.event.EventNotFoundException;
import org.example.taskFlow.exception.project.ProjectNotFoundException;
import org.example.taskFlow.exception.security.AccessTokenUsedInsteadOfRefreshTokenException;
import org.example.taskFlow.exception.task.TaskNotFoundException;
import org.example.taskFlow.exception.user.EmailAlreadyExistsException;
import org.example.taskFlow.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            TaskNotFoundException.class,
            UserNotFoundException.class,
            ProjectNotFoundException.class,
            CommentNotFoundException.class,
            MongoDbDocumentNotFoundException.class,
            EventNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse (
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            BindException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            IllegalArgumentException.class,
            AccessTokenUsedInsteadOfRefreshTokenException.class

    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Throwable ex) {
        String message;
        if (ex instanceof MethodArgumentNotValidException e) {
            message = "Ошибка валидаций: " + e.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
        } else if (ex instanceof HttpMessageNotReadableException) {
            message = "Неправильный JSON формат";
        } else if (ex instanceof BindException) {
            message = "Ошибка привязки: " + ((BindException) ex).getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
        } else if (ex instanceof MissingServletRequestParameterException) {
            message = "Отсутствует обязательный параметр: " + ((MissingServletRequestParameterException) ex).getParameterName();
        } else if (ex instanceof MethodArgumentTypeMismatchException) {
            message = "Неверный тип параметра: " + ((MethodArgumentTypeMismatchException) ex).getName();
        } else if (ex instanceof IllegalArgumentException) {
            message = "Неверный аргумент: " + ex.getMessage();
        } else {
            message = ex.getMessage();
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConflictException (EmailAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse (
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);

    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handlePayloadToLargeException(FileTooLargeException ex) {
        ErrorResponse errorResponse = new ErrorResponse (
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaTypeException (FileFormatNotSupportedException ex) {
        ErrorResponse errorResponse = new ErrorResponse (
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }
}