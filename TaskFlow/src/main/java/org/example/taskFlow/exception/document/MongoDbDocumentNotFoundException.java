package org.example.taskFlow.exception.document;

public class MongoDbDocumentNotFoundException extends RuntimeException {
    public MongoDbDocumentNotFoundException(String documentId ) {
        super(String.format(
                "Документ по id: %s не найден", documentId
        ));
    }
}
