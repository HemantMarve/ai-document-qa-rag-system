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

## LLM Answer Generation

Local mode uses extractive answers directly from retrieved chunks.

Production can enable OpenAI chat completions for synthesized answers over retrieved context:

```bash
APP_GENERATION_PROVIDER=openai
OPENAI_API_KEY=...
OPENAI_CHAT_MODEL=gpt-4o-mini
OPENAI_CHAT_MAX_TOKENS=500
```

The prompt instructs the model to answer only from retrieved context and to say when uploaded documents do not contain enough information.

## Helm Hardening

The chart includes production-oriented controls:

- Dedicated ServiceAccount.
- Pod and container security contexts.
- HorizontalPodAutoscaler.
- PodDisruptionBudget.
- Optional NetworkPolicy.

Enable NetworkPolicy only after confirming your ingress controller labels:

```bash
helm upgrade --install document-qa-rag ./charts/document-qa-rag \
  --set networkPolicy.enabled=true
```

## Next Ordered Items

After this baseline, the next production-hardening steps are:

1. Add Docker image build/publish workflow.
2. Add Terraform or cloud-specific infrastructure automation.
