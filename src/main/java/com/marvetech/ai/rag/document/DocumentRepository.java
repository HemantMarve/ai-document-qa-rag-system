package com.marvetech.ai.rag.document;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {
    List<DocumentEntity> findByTenantIdOrderByCreatedAtDesc(String tenantId);
    Optional<DocumentEntity> findByIdAndTenantId(UUID id, String tenantId);
}
