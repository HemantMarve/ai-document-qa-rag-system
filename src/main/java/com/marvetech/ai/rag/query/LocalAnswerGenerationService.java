package com.marvetech.ai.rag.query;

import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.generation.provider", havingValue = "local", matchIfMissing = true)
public class LocalAnswerGenerationService implements AnswerGenerationService {
    @Override
    public String generateAnswer(String question, List<SourceReference> sources) {
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
