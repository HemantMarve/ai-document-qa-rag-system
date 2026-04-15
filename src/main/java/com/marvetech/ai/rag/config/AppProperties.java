package com.marvetech.ai.rag.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Jwt jwt = new Jwt();
    private Ingestion ingestion = new Ingestion();
    private VectorStore vectorStore = new VectorStore();
    private Embeddings embeddings = new Embeddings();
    private Rag rag = new Rag();
    private List<DemoUser> demoUsers = new ArrayList<>();

    public Jwt getJwt() { return jwt; }
    public void setJwt(Jwt jwt) { this.jwt = jwt; }
    public Ingestion getIngestion() { return ingestion; }
    public void setIngestion(Ingestion ingestion) { this.ingestion = ingestion; }
    public VectorStore getVectorStore() { return vectorStore; }
    public void setVectorStore(VectorStore vectorStore) { this.vectorStore = vectorStore; }
    public Embeddings getEmbeddings() { return embeddings; }
    public void setEmbeddings(Embeddings embeddings) { this.embeddings = embeddings; }
    public Rag getRag() { return rag; }
    public void setRag(Rag rag) { this.rag = rag; }
    public List<DemoUser> getDemoUsers() { return demoUsers; }
    public void setDemoUsers(List<DemoUser> demoUsers) { this.demoUsers = demoUsers; }

    public static class Jwt {
        @NotBlank private String issuer;
        @NotBlank private String secret;
        @Min(1) private long ttlMinutes;
        public String getIssuer() { return issuer; }
        public void setIssuer(String issuer) { this.issuer = issuer; }
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getTtlMinutes() { return ttlMinutes; }
        public void setTtlMinutes(long ttlMinutes) { this.ttlMinutes = ttlMinutes; }
    }

    public static class Ingestion {
        @NotBlank private String mode = "async";
        @NotBlank private String topic = "document-ingestion-requests";
        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public boolean kafkaEnabled() { return "kafka".equalsIgnoreCase(mode); }
    }

    public static class VectorStore {
        @NotBlank private String provider = "local";
        private PgVector pgvector = new PgVector();
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public PgVector getPgvector() { return pgvector; }
        public void setPgvector(PgVector pgvector) { this.pgvector = pgvector; }
        public boolean pgvectorEnabled() { return "pgvector".equalsIgnoreCase(provider) || pgvector.isEnabled(); }
    }

    public static class PgVector {
        private boolean enabled;
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }

    public static class Embeddings {
        @NotBlank private String provider = "local";
        private OpenAi openai = new OpenAi();
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public OpenAi getOpenai() { return openai; }
        public void setOpenai(OpenAi openai) { this.openai = openai; }
        public boolean openAiEnabled() { return "openai".equalsIgnoreCase(provider); }
    }

    public static class OpenAi {
        private String apiKey = "";
        private String baseUrl = "https://api.openai.com";
        private String model = "text-embedding-3-small";
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
    }

    public static class Rag {
        @Min(100) private int chunkSize = 900;
        @Min(0) private int chunkOverlap = 120;
        @Min(32) private int embeddingDimensions = 256;
        public int getChunkSize() { return chunkSize; }
        public void setChunkSize(int chunkSize) { this.chunkSize = chunkSize; }
        public int getChunkOverlap() { return chunkOverlap; }
        public void setChunkOverlap(int chunkOverlap) { this.chunkOverlap = chunkOverlap; }
        public int getEmbeddingDimensions() { return embeddingDimensions; }
        public void setEmbeddingDimensions(int embeddingDimensions) { this.embeddingDimensions = embeddingDimensions; }
    }

    public static class DemoUser {
        private String username;
        private String password;
        private List<String> roles = new ArrayList<>();
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
    }
}
