package com.marvetech.ai.rag.query;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record QueryRequest(@NotBlank String question, @Positive Integer topK) {
    public int resolvedTopK() {
        return topK == null ? 5 : Math.min(topK, 20);
    }
}
