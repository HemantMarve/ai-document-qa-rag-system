CREATE EXTENSION IF NOT EXISTS vector;

ALTER TABLE document_chunks
    ADD COLUMN IF NOT EXISTS embedding_vector vector(256);

CREATE INDEX IF NOT EXISTS idx_document_chunks_embedding_vector
    ON document_chunks USING hnsw (embedding_vector vector_cosine_ops);
