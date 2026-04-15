package com.marvetech.ai.rag.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvetech.ai.rag.config.AppProperties;
import java.util.UUID;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class IngestionDispatcher {
    private final AppProperties properties;
    private final IngestionProcessor ingestionProcessor;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public IngestionDispatcher(AppProperties properties, IngestionProcessor ingestionProcessor, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.properties = properties;
        this.ingestionProcessor = ingestionProcessor;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void dispatch(UUID documentId, String tenantId) {
        if (!properties.getIngestion().kafkaEnabled()) {
            ingestionProcessor.process(documentId);
            return;
        }
        try {
            var payload = objectMapper.writeValueAsString(new DocumentIngestionEvent(documentId, tenantId));
            kafkaTemplate.send(properties.getIngestion().getTopic(), documentId.toString(), payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize ingestion event", ex);
        }
    }
}
