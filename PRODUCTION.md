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

## Original Document Storage

Local mode keeps extracted text in the database and records file size metadata.

Production can store original uploads in any S3-compatible object store:

```bash
APP_STORAGE_PROVIDER=s3
APP_STORAGE_S3_BUCKET=document-qa-rag-prod
APP_STORAGE_S3_REGION=us-east-1
APP_STORAGE_S3_ACCESS_KEY=...
APP_STORAGE_S3_SECRET_KEY=...
```

For MinIO or other S3-compatible endpoints:

```bash
APP_STORAGE_S3_ENDPOINT=https://minio.example.com
APP_STORAGE_S3_PATH_STYLE_ACCESS=true
```

The database stores the S3 object key and file size alongside document metadata.

## Next Ordered Items

After this baseline, the next production-hardening steps are:

1. Add LLM answer generation over retrieved context.
2. Harden Helm with service account, security context, HPA, PDB, and network policies.
3. Add Docker image build/publish workflow.
4. Add Terraform or cloud-specific infrastructure automation.
