package com.marvetech.ai.rag.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String tenantId, @NotBlank String username, @NotBlank String password) {}
