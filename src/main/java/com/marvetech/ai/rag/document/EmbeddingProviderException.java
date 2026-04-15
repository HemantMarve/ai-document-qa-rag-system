package com.marvetech.ai.rag.document;

public class EmbeddingProviderException extends RuntimeException {
    public EmbeddingProviderException(String message) {
        super(message);
    }

    public EmbeddingProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
