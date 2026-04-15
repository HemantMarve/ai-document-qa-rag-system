package com.marvetech.ai.rag.query;

import java.util.List;

public interface AnswerGenerationService {
    String generateAnswer(String question, List<SourceReference> sources);
}
