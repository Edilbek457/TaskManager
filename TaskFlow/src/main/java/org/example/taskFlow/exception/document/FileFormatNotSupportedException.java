package org.example.taskFlow.exception.document;

public class FileFormatNotSupportedException extends RuntimeException {
    public FileFormatNotSupportedException(String fileFormat) {
        super(String.format(
                "Файл с форматом %s не поддерживается", fileFormat
        ));
    }
}
