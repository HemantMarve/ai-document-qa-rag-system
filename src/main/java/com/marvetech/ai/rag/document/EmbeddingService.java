package com.marvetech.ai.rag.document;

import com.marvetech.ai.rag.config.AppProperties;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {
    private final AppProperties properties;
    private final OpenAiEmbeddingClient openAiEmbeddingClient;
    private final int dimensions;

    public EmbeddingService(AppProperties properties, OpenAiEmbeddingClient openAiEmbeddingClient) {
        this.properties = properties;
        this.openAiEmbeddingClient = openAiEmbeddingClient;
        this.dimensions = properties.getRag().getEmbeddingDimensions();
    }

    public double[] embed(String text) {
        if (properties.getEmbeddings().openAiEnabled()) {
            var vector = openAiEmbeddingClient.embed(text, dimensions);
            normalize(vector);
            return vector;
        }
        return localEmbed(text);
    }

    private double[] localEmbed(String text) {
        var vector = new double[dimensions];
        for (String token : tokenize(text)) {
            if (token.isBlank()) {
                continue;
            }
            int index = Math.floorMod(hash(token), dimensions);
            vector[index] += 1.0;
        }
        normalize(vector);
        return vector;
    }

    public double cosine(double[] left, double[] right) {
        double sum = 0.0;
        for (int i = 0; i < Math.min(left.length, right.length); i++) {
            sum += left[i] * right[i];
        }
        return sum;
    }

    public String serialize(double[] vector) {
        var builder = new StringBuilder();
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) builder.append(',');
            builder.append(vector[i]);
        }
        return builder.toString();
    }

    public double[] deserialize(String value) {
        if (value == null || value.isBlank()) {
            return new double[dimensions];
        }
        return Arrays.stream(value.split(",")).mapToDouble(Double::parseDouble).toArray();
    }

    private String[] tokenize(String text) {
        return text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").split("\\s+");
    }

    private int hash(String token) {
        try {
            var digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return ((digest[0] & 0xff) << 24) | ((digest[1] & 0xff) << 16) | ((digest[2] & 0xff) << 8) | (digest[3] & 0xff);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is required by the JVM", e);
        }
    }

    private void normalize(double[] vector) {
        double magnitude = 0.0;
        for (double value : vector) {
            magnitude += value * value;
        }
        magnitude = Math.sqrt(magnitude);
        if (magnitude == 0.0) {
            return;
        }
        for (int i = 0; i < vector.length; i++) {
            vector[i] /= magnitude;
        }
    }
}
