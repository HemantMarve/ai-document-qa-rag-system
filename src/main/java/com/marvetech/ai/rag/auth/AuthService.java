package com.marvetech.ai.rag.auth;

import com.marvetech.ai.rag.config.AppProperties;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Service
@ConditionalOnProperty(name = "app.auth.mode", havingValue = "demo", matchIfMissing = true)
public class AuthService {
    private final AppProperties properties;
    private final JwtEncoder jwtEncoder;

    public AuthService(AppProperties properties) {
        this.properties = properties;
        var key = new SecretKeySpec(properties.getJwt().getSecret().getBytes(), "HmacSHA256");
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }

    public LoginResponse login(LoginRequest request) {
        var user = properties.getDemoUsers().stream()
            .filter(candidate -> Objects.equals(candidate.getUsername(), request.username()))
            .filter(candidate -> Objects.equals(candidate.getPassword(), request.password()))
            .findFirst()
            .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        var now = Instant.now();
        var expiresAt = now.plusSeconds(properties.getJwt().getTtlMinutes() * 60);
        var claims = JwtClaimsSet.builder()
            .issuer(properties.getJwt().getIssuer())
            .issuedAt(now)
            .expiresAt(expiresAt)
            .subject(request.username())
            .claims(values -> values.putAll(Map.of(
                properties.getAuth().getTenantClaim(), request.tenantId(),
                properties.getAuth().getRolesClaim(), user.getRoles())))
            .build();
        var token = jwtEncoder.encode(org.springframework.security.oauth2.jwt.JwtEncoderParameters.from(
            JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new LoginResponse(token, "Bearer", expiresAt, request.tenantId(), request.username(), user.getRoles());
    }
}
