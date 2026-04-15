package com.marvetech.ai.rag.query;

import java.util.UUID;

public record SourceReference(UUID documentId, String filename, int chunkIndex, double score, String text) {}
