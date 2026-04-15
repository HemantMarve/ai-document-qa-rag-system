package com.marvetech.ai.rag.document;

import java.time.Instant;
import java.util.UUID;

public record DocumentResponse(UUID id, String filename, String contentType, DocumentStatus status, Instant createdAt, Instant processedAt, String errorMessage) {
    static DocumentResponse from(DocumentEntity document) {
        return new DocumentResponse(document.getId(), document.getFilename(), document.getContentType(), document.getStatus(), document.getCreatedAt(), document.getProcessedAt(), document.getErrorMessage());
    }
}
