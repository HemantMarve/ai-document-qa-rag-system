CREATE TABLE IF NOT EXISTS documents (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    owner_username VARCHAR(255) NOT NULL,
    filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(255),
    status VARCHAR(64) NOT NULL,
    raw_text TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    error_message VARCHAR(2048)
);

CREATE TABLE IF NOT EXISTS document_chunks (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    document_id UUID NOT NULL,
    chunk_index INTEGER NOT NULL,
    text TEXT NOT NULL,
    embedding TEXT NOT NULL,
    CONSTRAINT fk_document_chunks_document
        FOREIGN KEY (document_id) REFERENCES documents(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_documents_tenant_created_at
    ON documents(tenant_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_document_chunks_tenant
    ON document_chunks(tenant_id);

CREATE INDEX IF NOT EXISTS idx_document_chunks_document
    ON document_chunks(document_id);
