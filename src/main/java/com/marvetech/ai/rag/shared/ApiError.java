package com.marvetech.ai.rag.shared;

import java.time.Instant;

public record ApiError(Instant timestamp, int status, String error, String message) {}
