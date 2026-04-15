package com.marvetech.ai.rag.document;

import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.vector-store.provider", havingValue = "local", matchIfMissing = true)
public class NoOpVectorIndexService implements VectorIndexService {
    @Override
    public void index(UUID chunkId, double[] embedding) {
    }
}
