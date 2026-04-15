package com.marvetech.ai.rag.query;

import com.marvetech.ai.rag.shared.CurrentUser;
import org.springframework.stereotype.Service;

@Service
public class QueryService {
    private final VectorSearchService vectorSearchService;
    private final AnswerGenerationService answerGenerationService;
    private final CurrentUser currentUser;

    public QueryService(VectorSearchService vectorSearchService, AnswerGenerationService answerGenerationService, CurrentUser currentUser) {
        this.vectorSearchService = vectorSearchService;
        this.answerGenerationService = answerGenerationService;
        this.currentUser = currentUser;
    }

    public QueryResponse answer(QueryRequest request) {
        var sources = vectorSearchService.search(currentUser.tenantId(), request.question(), request.resolvedTopK());
        if (sources.isEmpty()) {
            return new QueryResponse("I could not find relevant context in this tenant's uploaded documents. Upload and process a document first, then retry the question.", sources);
        }
        return new QueryResponse(answerGenerationService.generateAnswer(request.question(), sources), sources);
    }
}
