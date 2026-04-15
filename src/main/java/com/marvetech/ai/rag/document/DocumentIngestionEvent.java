package com.marvetech.ai.rag.document;

import java.util.UUID;

public record DocumentIngestionEvent(UUID documentId, String tenantId) {}
