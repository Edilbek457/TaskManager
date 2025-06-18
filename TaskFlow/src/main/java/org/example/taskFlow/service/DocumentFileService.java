package org.example.taskFlow.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.taskFlow.component.FileProperties;
import org.example.taskFlow.exception.document.FileFormatNotSupportedException;
import org.example.taskFlow.exception.document.FileTooLargeException;
import org.example.taskFlow.exception.document.MongoDbDocumentNotFoundException;
import org.example.taskFlow.exception.task.TaskNotFoundException;
import org.example.taskFlow.model.DocumentFile;
import org.example.taskFlow.repository.DocumentFileRepository;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLConnection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentFileService {

    private final DocumentFileRepository documentFileRepository;
    private final GridFsService gridFsService;
    private final TaskService taskService;
    private final GridFsTemplate gridFsTemplate;
    private final FileProperties properties;

    public GridFSFile getFileById (ObjectId id) {
        GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
        if (gridFsFile == null) {
            throw new MongoDbDocumentNotFoundException(id.toHexString());
        }
        return gridFsFile;
    }

    public Resource getResource(GridFSFile file) {
        return gridFsTemplate.getResource(file);
    }

    public Resource downloadFile (ObjectId id) {
        return getResource(getFileById(id));
    }

    public List<DocumentFile> getAllDocumentFiles(long taskId) {
        return documentFileRepository.findDocumentFilesByTaskId(taskId);
    }

    public DocumentFile saveDocumentFile(long taskId, MultipartFile multipartFile) throws IOException {
        if (taskService.getTaskById(taskId) == null) {
            throw new TaskNotFoundException(taskId);
        }

        if (properties.getMaxSize() < multipartFile.getSize()) {
            throw new FileTooLargeException(multipartFile.getSize(), properties.getMaxSize());
        }

        if (!properties.getAllowedMimeTypes().contains(multipartFile.getContentType())) {
            throw new FileFormatNotSupportedException(multipartFile.getContentType());
        }

        ObjectId objectId = gridFsTemplate.store(
                multipartFile.getInputStream(),
                multipartFile.getOriginalFilename(),
                multipartFile.getContentType());

        DocumentFile documentFile = new DocumentFile();
        documentFile.setId(objectId);
        documentFile.setTaskId(taskId);
        documentFile.setFileName(multipartFile.getOriginalFilename());
        documentFile.setFileType(multipartFile.getContentType());
        documentFile.setSize(multipartFile.getSize());
        return documentFileRepository.save(documentFile);
    }

    public void deleteDocumentFile(String id) {
        ObjectId objectId = new ObjectId(id);
        if (getFileById(objectId) == null) {
            throw new MongoDbDocumentNotFoundException(id);
        }
        documentFileRepository.deleteById(objectId);
        gridFsService.deleteFile(objectId);
    }

    public MediaType detectMediaType(Resource resource) {
        try {
            String mimeType = URLConnection.guessContentTypeFromName(resource.getFilename());
            return mimeType != null ? MediaType.parseMediaType(mimeType) : MediaType.APPLICATION_OCTET_STREAM;
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}