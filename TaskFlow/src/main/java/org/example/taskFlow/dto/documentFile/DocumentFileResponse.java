package org.example.taskFlow.dto.documentFile;

import org.example.taskFlow.model.DocumentFile;
import java.time.LocalDateTime;

public record DocumentFileResponse(

        String id,
        long taskId,
        String fileName,
        String fileType,
        LocalDateTime uploadedAt,
        long size
) {
    public static DocumentFileResponse from (DocumentFile documentFile) {
        return new DocumentFileResponse(documentFile.getId().toHexString(), documentFile.getTaskId(), documentFile.getFileName(), documentFile.getFileType(), documentFile.getUploadedAt(), documentFile.getSize());
    }
}
