ALTER TABLE documents
    ADD COLUMN IF NOT EXISTS object_key VARCHAR(1024);

ALTER TABLE documents
    ADD COLUMN IF NOT EXISTS size_bytes BIGINT;

CREATE INDEX IF NOT EXISTS idx_documents_object_key
    ON documents(object_key);
