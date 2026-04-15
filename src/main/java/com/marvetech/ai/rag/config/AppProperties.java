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
    private Auth auth = new Auth();
    private Jwt jwt = new Jwt();
    private Storage storage = new Storage();
    private Ingestion ingestion = new Ingestion();
    private VectorStore vectorStore = new VectorStore();
    private Embeddings embeddings = new Embeddings();
    private Generation generation = new Generation();
    private Rag rag = new Rag();
    private List<DemoUser> demoUsers = new ArrayList<>();

    public Auth getAuth() { return auth; }
    public void setAuth(Auth auth) { this.auth = auth; }
    public Jwt getJwt() { return jwt; }
    public void setJwt(Jwt jwt) { this.jwt = jwt; }
    public Storage getStorage() { return storage; }
    public void setStorage(Storage storage) { this.storage = storage; }
    public Ingestion getIngestion() { return ingestion; }
    public void setIngestion(Ingestion ingestion) { this.ingestion = ingestion; }
    public VectorStore getVectorStore() { return vectorStore; }
    public void setVectorStore(VectorStore vectorStore) { this.vectorStore = vectorStore; }
    public Embeddings getEmbeddings() { return embeddings; }
    public void setEmbeddings(Embeddings embeddings) { this.embeddings = embeddings; }
    public Generation getGeneration() { return generation; }
    public void setGeneration(Generation generation) { this.generation = generation; }
    public Rag getRag() { return rag; }
    public void setRag(Rag rag) { this.rag = rag; }
    public List<DemoUser> getDemoUsers() { return demoUsers; }
    public void setDemoUsers(List<DemoUser> demoUsers) { this.demoUsers = demoUsers; }

    public static class Auth {
        @NotBlank private String mode = "demo";
        @NotBlank private String tenantClaim = "tenant_id";
        @NotBlank private String rolesClaim = "roles";
        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
        public String getTenantClaim() { return tenantClaim; }
        public void setTenantClaim(String tenantClaim) { this.tenantClaim = tenantClaim; }
        public String getRolesClaim() { return rolesClaim; }
        public void setRolesClaim(String rolesClaim) { this.rolesClaim = rolesClaim; }
        public boolean demoMode() { return "demo".equalsIgnoreCase(mode); }
    }

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

    public static class Storage {
        @NotBlank private String provider = "database";
        private S3 s3 = new S3();
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public S3 getS3() { return s3; }
        public void setS3(S3 s3) { this.s3 = s3; }
        public boolean s3Enabled() { return "s3".equalsIgnoreCase(provider); }
    }

    public static class S3 {
        private String bucket = "";
        private String region = "us-east-1";
        private String endpoint = "";
        private String accessKey = "";
        private String secretKey = "";
        private boolean pathStyleAccess;
        public String getBucket() { return bucket; }
        public void setBucket(String bucket) { this.bucket = bucket; }
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        public String getAccessKey() { return accessKey; }
        public void setAccessKey(String accessKey) { this.accessKey = accessKey; }
        public String getSecretKey() { return secretKey; }
        public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
        public boolean isPathStyleAccess() { return pathStyleAccess; }
        public void setPathStyleAccess(boolean pathStyleAccess) { this.pathStyleAccess = pathStyleAccess; }
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

    public static class Generation {
        @NotBlank private String provider = "local";
        private OpenAiGeneration openai = new OpenAiGeneration();
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public OpenAiGeneration getOpenai() { return openai; }
        public void setOpenai(OpenAiGeneration openai) { this.openai = openai; }
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

    public static class OpenAiGeneration {
        private String apiKey = "";
        private String baseUrl = "https://api.openai.com";
        private String model = "gpt-4o-mini";
        @Min(64) private int maxTokens = 500;
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
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
