package com.marvetech.ai.rag.document;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "document_chunks")
public class DocumentChunkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false) private String tenantId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private DocumentEntity document;
    @Column(nullable = false) private int chunkIndex;
    @Lob @Column(nullable = false) private String text;
    @Lob @Column(nullable = false) private String embedding;

    public UUID getId() { return id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public DocumentEntity getDocument() { return document; }
    public void setDocument(DocumentEntity document) { this.document = document; }
    public int getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(int chunkIndex) { this.chunkIndex = chunkIndex; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getEmbedding() { return embedding; }
    public void setEmbedding(String embedding) { this.embedding = embedding; }
}
