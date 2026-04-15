# Production Deployment Path

This repo now supports a safer production baseline while preserving local demo mode.

## Profiles

Local development:

```bash
mvn spring-boot:run
```

Production-style runtime:

```bash
SPRING_PROFILES_ACTIVE=postgres,prod \
APP_AUTH_MODE=oidc \
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=https://issuer.example.com/ \
SPRING_DATASOURCE_URL=jdbc:postgresql://db.example.com:5432/ragdb \
SPRING_DATASOURCE_USERNAME=rag \
SPRING_DATASOURCE_PASSWORD=... \
java -jar document-qa-rag-system.jar
```

## Database Migrations

Flyway owns schema creation. The `prod` profile sets Hibernate to `validate` so the application fails fast if schema drift exists.

Migration locations:

- `classpath:db/migration` for portable schema.
- `classpath:db/migration/postgres` for PostgreSQL and pgvector additions.

PostgreSQL deployments should use `SPRING_PROFILES_ACTIVE=postgres,prod` so the pgvector migration runs.

## Authentication Modes

`APP_AUTH_MODE=demo` is intended only for local development and exposes `/api/auth/login` backed by configured demo users.

`APP_AUTH_MODE=oidc` disables demo login and expects externally issued JWTs from an OIDC provider such as Auth0, Cognito, Keycloak, Okta, or Azure AD.

Required OIDC setting:

```bash
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=https://issuer.example.com/
```

Optional claim mapping:

```bash
APP_AUTH_TENANT_CLAIM=tenant_id
APP_AUTH_ROLES_CLAIM=roles
```

Tokens must include the configured tenant claim, otherwise tenant-scoped APIs reject the request.

## Next Ordered Items

After this baseline, the next production-hardening steps are:

1. Add S3-compatible object storage for original documents.
2. Add LLM answer generation over retrieved context.
3. Harden Helm with service account, security context, HPA, PDB, and network policies.
4. Add Docker image build/publish workflow.
5. Add Terraform or cloud-specific infrastructure automation.
