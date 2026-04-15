package com.marvetech.ai.rag.document;

import java.time.Instant;
import java.util.UUID;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngestionProcessor {
    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;
    private final ChunkingService chunkingService;
    private final EmbeddingService embeddingService;
    private final VectorIndexService vectorIndexService;

    public IngestionProcessor(DocumentRepository documentRepository, DocumentChunkRepository chunkRepository, ChunkingService chunkingService, EmbeddingService embeddingService, VectorIndexService vectorIndexService) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.chunkingService = chunkingService;
        this.embeddingService = embeddingService;
        this.vectorIndexService = vectorIndexService;
    }

    @Async
    @Transactional
    public void process(UUID documentId) {
        var document = documentRepository.findById(documentId).orElseThrow();
        try {
            document.setStatus(DocumentStatus.PROCESSING);
            chunkRepository.deleteByDocument(document);
            var chunks = chunkingService.chunk(document.getRawText());
            for (int i = 0; i < chunks.size(); i++) {
                var chunk = new DocumentChunkEntity();
                chunk.setTenantId(document.getTenantId());
                chunk.setDocument(document);
                chunk.setChunkIndex(i);
                chunk.setText(chunks.get(i));
                var embedding = embeddingService.embed(chunks.get(i));
                chunk.setEmbedding(embeddingService.serialize(embedding));
                var savedChunk = chunkRepository.save(chunk);
                vectorIndexService.index(savedChunk.getId(), embedding);
            }
            document.setStatus(DocumentStatus.READY);
            document.setProcessedAt(Instant.now());
            document.setErrorMessage(null);
        } catch (Exception ex) {
            document.setStatus(DocumentStatus.FAILED);
            document.setErrorMessage(ex.getMessage());
        }
    }
}
