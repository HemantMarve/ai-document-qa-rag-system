package com.marvetech.ai.rag.query;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.marvetech.ai.rag.config.AppProperties;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@ConditionalOnProperty(name = "app.generation.provider", havingValue = "openai")
public class OpenAiAnswerGenerationService implements AnswerGenerationService {
    private final AppProperties properties;

    public OpenAiAnswerGenerationService(AppProperties properties) {
        this.properties = properties;
    }

    @Override
    public String generateAnswer(String question, List<SourceReference> sources) {
        var config = properties.getGeneration().getOpenai();
        if (config.getApiKey() == null || config.getApiKey().isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY is required when APP_GENERATION_PROVIDER=openai");
        }
        var client = RestClient.builder()
            .baseUrl(config.getBaseUrl())
            .defaultHeader("Authorization", "Bearer " + config.getApiKey())
            .build();
        var response = client.post()
            .uri("/v1/chat/completions")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Map.of(
                "model", config.getModel(),
                "max_tokens", config.getMaxTokens(),
                "temperature", 0.1,
                "messages", List.of(
                    Map.of("role", "developer", "content", "Answer only from the provided document context. If the context is insufficient, say that the uploaded documents do not contain enough information. Include short source markers like [1] when useful."),
                    Map.of("role", "user", "content", buildPrompt(question, sources)))))
            .retrieve()
            .body(ChatCompletionResponse.class);
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new IllegalStateException("OpenAI chat completion response did not contain an answer");
        }
        return response.choices().getFirst().message().content();
    }

    private String buildPrompt(String question, List<SourceReference> sources) {
        var builder = new StringBuilder();
        builder.append("Question: ").append(question).append("\n\nDocument context:\n");
        for (int i = 0; i < sources.size(); i++) {
            var source = sources.get(i);
            builder.append("[").append(i + 1).append("] ")
                .append("file=").append(source.filename())
                .append(", chunk=").append(source.chunkIndex())
                .append(", score=").append(source.score())
                .append("\n")
                .append(source.text())
                .append("\n\n");
        }
        return builder.toString();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ChatCompletionResponse(List<Choice> choices) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Choice(Message message) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Message(String content) {}
}
