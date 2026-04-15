package com.marvetech.ai.rag.query;

import com.marvetech.ai.rag.document.DocumentChunkEntity;
import com.marvetech.ai.rag.document.DocumentChunkRepository;
import com.marvetech.ai.rag.document.DocumentStatus;
import com.marvetech.ai.rag.document.EmbeddingService;
import java.util.Comparator;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(name = "app.vector-store.provider", havingValue = "local", matchIfMissing = true)
public class LocalVectorSearchService implements VectorSearchService {
    private final DocumentChunkRepository chunkRepository;
    private final EmbeddingService embeddingService;

    public LocalVectorSearchService(DocumentChunkRepository chunkRepository, EmbeddingService embeddingService) {
        this.chunkRepository = chunkRepository;
        this.embeddingService = embeddingService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SourceReference> search(String tenantId, String question, int topK) {
        var questionEmbedding = embeddingService.embed(question);
        return chunkRepository.findByTenantId(tenantId).stream()
            .filter(chunk -> chunk.getDocument().getStatus() == DocumentStatus.READY)
            .map(chunk -> toScoredSource(chunk, questionEmbedding))
            .filter(source -> source.score() > 0.0)
            .sorted(Comparator.comparingDouble(SourceReference::score).reversed())
            .limit(topK)
            .toList();
    }

    private SourceReference toScoredSource(DocumentChunkEntity chunk, double[] questionEmbedding) {
        double score = embeddingService.cosine(questionEmbedding, embeddingService.deserialize(chunk.getEmbedding()));
        return new SourceReference(chunk.getDocument().getId(), chunk.getDocument().getFilename(), chunk.getChunkIndex(), score, chunk.getText());
    }
}
