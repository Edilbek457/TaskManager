package org.example.taskFlow.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.taskFlow.exception.document.MongoDbDocumentNotFoundException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GridFsService {

    private final GridFsTemplate gridFsTemplate;

    public void deleteFile(ObjectId id) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
        if (gridFSFile == null) {
            throw new MongoDbDocumentNotFoundException(id.toHexString());
        } gridFsTemplate.delete(Query.query(Criteria.where("_id").is(id)));
    }

}
