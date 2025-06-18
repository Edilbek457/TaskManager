package org.example.taskFlow.unit.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.BsonInt32;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.taskFlow.exception.document.MongoDbDocumentNotFoundException;
import org.example.taskFlow.service.oldService.GridFsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GridFsServiceTest {

    @Mock
    private GridFSFile gridFsFile;

    @Mock
    private GridFsTemplate gridFsTemplate;

    @InjectMocks
    private GridFsService gridFsService;

    private final static Date UPLOAD_DATE = new Date();

    @Test
    public void deleteFile_whenFileExists_thenFileDeleted() {
        ObjectId objectId = new ObjectId("0123456789abcdef01234567");
        GridFSFile gridFSFile = new GridFSFile(new BsonInt32(12), "file", 12, 255, UPLOAD_DATE, new Document());

        when(gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(objectId)))).thenReturn(gridFSFile);

        gridFsService.deleteFile(objectId);

        verify(gridFsTemplate).findOne(Query.query(Criteria.where("_id").is(objectId)));
        verify(gridFsTemplate).delete(Query.query(Criteria.where("_id").is(objectId)));
    }



    @Test
    public void deleteFile_whenFileNotExist_thenThrowException() {
        ObjectId nonExistentId = new ObjectId("0123456789abcdef01234567");
        when(gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(nonExistentId)))).thenReturn(null);

        assertThrows(MongoDbDocumentNotFoundException.class, () -> gridFsService.deleteFile(nonExistentId));

        verify(gridFsTemplate).findOne(Query.query(Criteria.where("_id").is(nonExistentId)));
        verify(gridFsTemplate, never()).delete(any());
    }

}
