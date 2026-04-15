package com.marvetech.ai.rag.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.marvetech.ai.rag.config.AppProperties;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenAiEmbeddingClient {
    private final AppProperties properties;

    public OpenAiEmbeddingClient(AppProperties properties) {
        this.properties = properties;
    }

    public double[] embed(String text, int dimensions) {
        var config = properties.getEmbeddings().getOpenai();
        if (config.getApiKey() == null || config.getApiKey().isBlank()) {
            throw new EmbeddingProviderException("OPENAI_API_KEY is required when APP_EMBEDDING_PROVIDER=openai");
        }
        try {
            var client = RestClient.builder()
                .baseUrl(config.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + config.getApiKey())
                .build();
            var response = client.post()
                .uri("/v1/embeddings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                    "model", config.getModel(),
                    "input", text,
                    "encoding_format", "float",
                    "dimensions", dimensions))
                .retrieve()
                .body(OpenAiEmbeddingResponse.class);
            if (response == null || response.data() == null || response.data().isEmpty()) {
                throw new EmbeddingProviderException("OpenAI embeddings response did not contain an embedding");
            }
            return response.data().getFirst().embedding().stream().mapToDouble(Double::doubleValue).toArray();
        } catch (EmbeddingProviderException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new EmbeddingProviderException("OpenAI embedding request failed", ex);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record OpenAiEmbeddingResponse(List<OpenAiEmbeddingData> data, String model) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record OpenAiEmbeddingData(int index, List<Double> embedding) {}
}
