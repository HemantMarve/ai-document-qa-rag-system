package com.marvetech.ai.rag;

import static org.assertj.core.api.Assertions.assertThat;

import com.marvetech.ai.rag.config.AppProperties;
import com.marvetech.ai.rag.document.ChunkingService;
import com.marvetech.ai.rag.document.EmbeddingService;
import com.marvetech.ai.rag.document.OpenAiEmbeddingClient;
import org.junit.jupiter.api.Test;

class EmbeddingServiceTest {
    @Test
    void similarTextHasPositiveSimilarity() {
        var properties = new AppProperties();
        properties.getRag().setEmbeddingDimensions(128);
        var service = new EmbeddingService(properties, new OpenAiEmbeddingClient(properties));

        var left = service.embed("documents are chunked and embedded for semantic search");
        var right = service.embed("semantic search uses embedded document chunks");

        assertThat(service.cosine(left, right)).isGreaterThan(0.0);
        assertThat(service.deserialize(service.serialize(left))).hasSize(128);
    }

    @Test
    void chunkingUsesConfiguredSizeAndOverlap() {
        var properties = new AppProperties();
        properties.getRag().setChunkSize(120);
        properties.getRag().setChunkOverlap(20);
        var chunking = new ChunkingService(properties);

        var chunks = chunking.chunk("a".repeat(260));

        assertThat(chunks).hasSizeGreaterThan(1);
        assertThat(chunks).allMatch(chunk -> chunk.length() <= 120);
    }
}
