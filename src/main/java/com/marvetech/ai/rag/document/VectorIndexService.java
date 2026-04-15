package com.marvetech.ai.rag.document;

import java.util.UUID;

public interface VectorIndexService {
    void index(UUID chunkId, double[] embedding);
}
