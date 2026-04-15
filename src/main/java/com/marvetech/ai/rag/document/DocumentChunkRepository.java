package com.marvetech.ai.rag.document;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunkEntity, UUID> {
    List<DocumentChunkEntity> findByTenantId(String tenantId);
    void deleteByDocument(DocumentEntity document);
}
