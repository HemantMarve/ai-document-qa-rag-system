package com.marvetech.ai.rag.auth;

import java.time.Instant;
import java.util.List;

public record LoginResponse(String accessToken, String tokenType, Instant expiresAt, String tenantId, String username, List<String> roles) {}
