package com.marvetech.ai.rag.shared;

import com.marvetech.ai.rag.config.AppProperties;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    private final AppProperties properties;

    public CurrentUser(AppProperties properties) {
        this.properties = properties;
    }

    public String username() {
        return jwt().getSubject();
    }

    public String tenantId() {
        var tenantId = jwt().getClaimAsString(properties.getAuth().getTenantClaim());
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("Authenticated token is missing tenant claim: " + properties.getAuth().getTenantClaim());
        }
        return tenantId;
    }

    public List<String> roles() {
        return jwt().getClaimAsStringList(properties.getAuth().getRolesClaim());
    }

    private Jwt jwt() {
        return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
