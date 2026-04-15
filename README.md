# AI Document Q&A System (RAG-based)

Production-shaped, local-testable multi-tenant RAG backend for document upload, async processing, semantic retrieval, and grounded Q&A.

## What Is Implemented

- Java 21 + Spring Boot backend under package `com.marvetech.ai.rag`.
- JWT login with tenant claim and role-based access control.
- Tenant-isolated document upload and listing APIs.
- Text extraction for `.txt` and PDF documents using PDFBox.
- Async ingestion pipeline that chunks documents and creates deterministic local embeddings.
- Kafka-backed ingestion mode for deployment-style asynchronous processing.
- Local vector search using cosine similarity over persisted chunk embeddings.
- Optional pgvector-backed semantic search for PostgreSQL deployments.
- Optional OpenAI embeddings provider using the OpenAI embeddings API.
- Query API returning a grounded answer plus source references.
- H2 default profile for fastest local testing.
- PostgreSQL profile for Docker Compose and Kubernetes.
- Dockerfile, Docker Compose, CI workflow, smoke-test script, and Helm chart.

## Local Run Without Docker

Requires Java 21 and Maven.

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080` and uses a local H2 database at `./data/ragdb`.

## Local Run With Docker Compose

```bash
docker compose up --build
```

Compose starts:

- API
- PostgreSQL
- Redis
- Kafka

Compose runs the deployment-shaped path by default:

- Kafka ingestion via `APP_INGESTION_MODE=kafka`
- pgvector search via `APP_VECTOR_STORE=pgvector`
- local deterministic embeddings via `APP_EMBEDDING_PROVIDER=local`

To use OpenAI embeddings in Compose, export an API key first:

```bash
export APP_EMBEDDING_PROVIDER=openai
export OPENAI_API_KEY=your_api_key_here
docker compose up --build
```

## Smoke Test

After the API is running:

```bash
./scripts/smoke-test.sh
```

The script logs in, uploads a sample document, waits briefly for processing, and asks a RAG question.

## Manual API Test

Login:

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"tenantId":"tenant-a","username":"admin","password":"admin123"}' \
  | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')
```

Upload a document:

```bash
echo "RAG retrieves relevant chunks before generating an answer." > /tmp/rag.txt
curl -X POST http://localhost:8080/api/documents \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/tmp/rag.txt"
```

Ask a question:

```bash
curl -X POST http://localhost:8080/api/query \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"question":"What does RAG retrieve?","topK":3}'
```

## Default Demo Users

- `admin` / `admin123` with roles `ADMIN`, `USER`
- `user` / `user123` with role `USER`

Use any `tenantId` during login. Data is isolated by that tenant claim.

## Runtime Modes

The app is intentionally configurable so local development and production deployment use the same codebase.

| Concern | Local default | Deployment option |
| --- | --- | --- |
| Ingestion | `APP_INGESTION_MODE=async` | `APP_INGESTION_MODE=kafka` |
| Vector search | `APP_VECTOR_STORE=local` | `APP_VECTOR_STORE=pgvector` |
| Embeddings | `APP_EMBEDDING_PROVIDER=local` | `APP_EMBEDDING_PROVIDER=openai` |

OpenAI embedding settings:

```bash
APP_EMBEDDING_PROVIDER=openai
OPENAI_API_KEY=your_api_key_here
OPENAI_EMBEDDING_MODEL=text-embedding-3-small
```

pgvector settings:

```bash
APP_VECTOR_STORE=pgvector
APP_RAG_EMBEDDING_DIMENSIONS=256
```

The pgvector mode expects PostgreSQL with the `vector` extension available. Compose and Helm use `pgvector/pgvector:pg16` for that reason.

## Helm Deployment

Render templates locally:

```bash
helm template document-qa-rag ./charts/document-qa-rag
```

Install into Kubernetes:

```bash
helm upgrade --install document-qa-rag ./charts/document-qa-rag \
  --set image.repository=marvetech/document-qa-rag-system \
  --set image.tag=0.1.0 \
  --set app.jwtSecret='replace-with-a-strong-secret'
```

The chart includes the API, PostgreSQL with pgvector, Redis, and Kafka manifests. For production, replace the bundled PostgreSQL/Kafka/Redis with managed services or hardened platform charts.

OpenAI embeddings with Helm:

```bash
helm upgrade --install document-qa-rag ./charts/document-qa-rag \
  --set app.embeddingProvider=openai \
  --set app.openAiApiKey='your_api_key_here' \
  --set app.jwtSecret='replace-with-a-strong-secret'
```

## Important Production Notes

- Replace demo users with a real identity provider or user store before any real deployment.
- OpenAI embeddings and pgvector are implemented as opt-in production paths.
- Kafka producers/consumers are implemented for ingestion, but should be load-tested and given dead-letter/retry policies before production traffic.
- Consider Pinecone, Weaviate, or a managed pgvector PostgreSQL service if you do not want to operate vector infrastructure yourself.
- Store secrets in Kubernetes Secrets managed by your cloud or secret manager.
