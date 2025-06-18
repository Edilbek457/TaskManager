package org.example.taskFlow.exception.document;

public class FileTooLargeException extends RuntimeException {
    public FileTooLargeException(long actualFileSize, long maxAllowedSize) {
        super(String.format(
                "Файл слишком большой (%d байт). Максимально допустимый размер: %s",
                actualFileSize, maxAllowedSize
        ));
    }
}

