package org.example.taskFlow.controller.oldController;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.taskFlow.dto.documentFile.DocumentFileResponse;
import org.example.taskFlow.model.DocumentFile;
import org.example.taskFlow.service.DocumentFileService;
import org.example.taskFlow.service.LogEntryService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DocumentFileController {

    private final DocumentFileService documentFileService;
    private final LogEntryService logEntryService;

    @GetMapping("/documents/{id}")
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<Resource> downloadDocument(@PathVariable ObjectId id) {
        Resource resource = documentFileService.downloadFile(id);
        String encodedFilename = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(documentFileService.detectMediaType(resource))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .body(resource);
    }

    @GetMapping("/task/{taskId}/documents")
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<List<DocumentFileResponse>> getDocumentFiles (@PathVariable long taskId) {
        List<DocumentFile> documentFileList = documentFileService.getAllDocumentFiles(taskId);
        List<DocumentFileResponse> documentFileResponses = documentFileList.stream()
                .map(DocumentFileResponse::from)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(documentFileResponses);
    }

    @PostMapping("/tasks/{taskId}/documents")
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<DocumentFileResponse> createDocumentFile(
            @PathVariable long taskId,
            @RequestParam("file") MultipartFile multipartFile) throws IOException {

        DocumentFile savedDocumentFile = documentFileService.saveDocumentFile(taskId, multipartFile);
        DocumentFileResponse documentFileResponse = DocumentFileResponse.from(savedDocumentFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(documentFileResponse);
    }

    @DeleteMapping("/documents/{id}")
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<Void> deleteDocumentFile (@PathVariable String id) {
        documentFileService.deleteDocumentFile(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
