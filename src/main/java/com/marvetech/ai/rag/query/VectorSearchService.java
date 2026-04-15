package com.marvetech.ai.rag.query;

import java.util.List;

public interface VectorSearchService {
    List<SourceReference> search(String tenantId, String question, int topK);
}
