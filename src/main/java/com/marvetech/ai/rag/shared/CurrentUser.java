package com.marvetech.ai.rag.shared;

import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    public String username() {
        return jwt().getSubject();
    }

    public String tenantId() {
        return jwt().getClaimAsString("tenant_id");
    }

    public List<String> roles() {
        return jwt().getClaimAsStringList("roles");
    }

    private Jwt jwt() {
        return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
