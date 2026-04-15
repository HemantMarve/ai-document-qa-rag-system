package com.marvetech.ai.rag.query;

import com.marvetech.ai.rag.config.AppProperties;
import com.marvetech.ai.rag.document.EmbeddingService;
import com.marvetech.ai.rag.document.VectorIndexService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.vector-store.provider", havingValue = "pgvector")
public class PgVectorSearchAndIndexService implements VectorSearchService, VectorIndexService {
    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingService embeddingService;
    private final AppProperties properties;

    public PgVectorSearchAndIndexService(JdbcTemplate jdbcTemplate, EmbeddingService embeddingService, AppProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingService = embeddingService;
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    void initializeSchema() {
        int dimensions = properties.getRag().getEmbeddingDimensions();
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
        jdbcTemplate.execute("ALTER TABLE document_chunks ADD COLUMN IF NOT EXISTS embedding_vector vector(" + dimensions + ")");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_document_chunks_embedding_vector ON document_chunks USING hnsw (embedding_vector vector_cosine_ops)");
    }

    @Override
    public void index(UUID chunkId, double[] embedding) {
        jdbcTemplate.update("UPDATE document_chunks SET embedding_vector = CAST(? AS vector) WHERE id = ?", toVectorLiteral(embedding), chunkId);
    }

    @Override
    public List<SourceReference> search(String tenantId, String question, int topK) {
        var queryVector = toVectorLiteral(embeddingService.embed(question));
        return jdbcTemplate.query("""
            SELECT c.id, c.chunk_index, c.text, d.id AS document_id, d.filename,
                   1 - (c.embedding_vector <=> CAST(? AS vector)) AS score
            FROM document_chunks c
            JOIN documents d ON d.id = c.document_id
            WHERE c.tenant_id = ?
              AND d.tenant_id = ?
              AND d.status = 'READY'
              AND c.embedding_vector IS NOT NULL
            ORDER BY c.embedding_vector <=> CAST(? AS vector)
            LIMIT ?
            """, (rs, rowNum) -> mapSource(rs), queryVector, tenantId, tenantId, queryVector, topK);
    }

    private SourceReference mapSource(ResultSet rs) throws SQLException {
        return new SourceReference(
            UUID.fromString(rs.getString("document_id")),
            rs.getString("filename"),
            rs.getInt("chunk_index"),
            rs.getDouble("score"),
            rs.getString("text"));
    }

    private String toVectorLiteral(double[] embedding) {
        var builder = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) builder.append(',');
            builder.append(embedding[i]);
        }
        return builder.append(']').toString();
    }
}
