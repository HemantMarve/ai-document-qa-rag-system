package com.marvetech.ai.rag.document;

import com.marvetech.ai.rag.config.AppProperties;
import java.io.IOException;
import java.net.URI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "s3")
public class S3OriginalDocumentStorage implements OriginalDocumentStorage {
    private final AppProperties properties;
    private final S3Client s3Client;

    public S3OriginalDocumentStorage(AppProperties properties) {
        this.properties = properties;
        this.s3Client = buildClient(properties.getStorage().getS3());
    }

    @Override
    public StoredDocument store(DocumentEntity document, MultipartFile file) throws IOException {
        var config = properties.getStorage().getS3();
        if (config.getBucket() == null || config.getBucket().isBlank()) {
            throw new IllegalStateException("APP_STORAGE_S3_BUCKET is required when APP_STORAGE_PROVIDER=s3");
        }
        var objectKey = document.getTenantId() + "/" + document.getId() + "/" + sanitize(document.getFilename());
        var request = PutObjectRequest.builder()
            .bucket(config.getBucket())
            .key(objectKey)
            .contentType(file.getContentType())
            .contentLength(file.getSize())
            .build();
        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
        return new StoredDocument(objectKey, file.getSize());
    }

    private S3Client buildClient(AppProperties.S3 config) {
        var builder = S3Client.builder()
            .region(Region.of(config.getRegion()))
            .forcePathStyle(config.isPathStyleAccess());
        if (config.getEndpoint() != null && !config.getEndpoint().isBlank()) {
            builder.endpointOverride(URI.create(config.getEndpoint()));
        }
        if (config.getAccessKey() != null && !config.getAccessKey().isBlank()) {
            builder.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey())));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
        return builder.build();
    }

    private String sanitize(String filename) {
        return filename == null ? "document" : filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
