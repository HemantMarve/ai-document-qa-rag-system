package com.marvetech.ai.rag.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvetech.ai.rag.config.AppProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.ingestion.mode", havingValue = "kafka")
public class IngestionKafkaConsumer {
    private final AppProperties properties;
    private final IngestionProcessor ingestionProcessor;
    private final ObjectMapper objectMapper;

    public IngestionKafkaConsumer(AppProperties properties, IngestionProcessor ingestionProcessor, ObjectMapper objectMapper) {
        this.properties = properties;
        this.ingestionProcessor = ingestionProcessor;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.ingestion.topic}")
    public void consume(String payload) throws Exception {
        var event = objectMapper.readValue(payload, DocumentIngestionEvent.class);
        ingestionProcessor.process(event.documentId());
    }
}
