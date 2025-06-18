package org.example.taskFlow.exception.elastic_search;

import java.util.UUID;

public class ElasticSearchDocumentNotFoundException extends RuntimeException {
    public ElasticSearchDocumentNotFoundException(UUID id) {
        super(String.format(
                "Документ по id: %s не найден", id)
        );
    }
}
