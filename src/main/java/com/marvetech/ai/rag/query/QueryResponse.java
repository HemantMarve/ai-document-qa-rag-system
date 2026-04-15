package com.marvetech.ai.rag.query;

import java.util.List;

public record QueryResponse(String answer, List<SourceReference> sources) {}
