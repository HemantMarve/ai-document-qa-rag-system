#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"
TENANT_ID="${TENANT_ID:-tenant-a}"
TOKEN=$(curl -sS -X POST "$BASE_URL/api/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"tenantId\":\"$TENANT_ID\",\"username\":\"admin\",\"password\":\"admin123\"}" | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')
if [[ -z "$TOKEN" ]]; then
  echo "Login failed" >&2
  exit 1
fi
DOC_FILE=$(mktemp)
cat > "$DOC_FILE" <<'DOC'
Retrieval augmented generation combines semantic search with language model generation. Documents are chunked, embedded, and stored in a vector database. At query time, relevant chunks are retrieved and used as grounded context for the answer.
DOC
curl -sS -X POST "$BASE_URL/api/documents" -H "Authorization: Bearer $TOKEN" -F "file=@$DOC_FILE;filename=rag-notes.txt" >/dev/null
sleep 2
curl -sS -X POST "$BASE_URL/api/query" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"question":"What is retrieval augmented generation?","topK":3}'
echo
