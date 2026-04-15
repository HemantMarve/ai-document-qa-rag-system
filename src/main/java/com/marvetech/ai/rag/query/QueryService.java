package com.marvetech.ai.rag.query;

import com.marvetech.ai.rag.shared.CurrentUser;
import org.springframework.stereotype.Service;

@Service
public class QueryService {
    private final VectorSearchService vectorSearchService;
    private final CurrentUser currentUser;

    public QueryService(VectorSearchService vectorSearchService, CurrentUser currentUser) {
        this.vectorSearchService = vectorSearchService;
        this.currentUser = currentUser;
    }

    public QueryResponse answer(QueryRequest request) {
        var sources = vectorSearchService.search(currentUser.tenantId(), request.question(), request.resolvedTopK());
        if (sources.isEmpty()) {
            return new QueryResponse("I could not find relevant context in this tenant's uploaded documents. Upload and process a document first, then retry the question.", sources);
        }
        var answer = buildGroundedAnswer(request.question(), sources);
        return new QueryResponse(answer, sources);
    }

    private String buildGroundedAnswer(String question, java.util.List<SourceReference> sources) {
        var builder = new StringBuilder();
        builder.append("Based on the uploaded documents, the most relevant context for '").append(question).append("' is: ");
        for (int i = 0; i < sources.size(); i++) {
            var source = sources.get(i);
            if (i > 0) builder.append(" ");
            builder.append("[").append(i + 1).append("] ").append(trim(source.text(), 420));
        }
        builder.append(" Sources are returned separately with document IDs, filenames, chunk indexes, and similarity scores.");
        return builder.toString();
    }

    private String trim(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}
