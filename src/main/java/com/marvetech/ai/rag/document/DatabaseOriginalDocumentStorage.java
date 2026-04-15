package com.marvetech.ai.rag.document;

import java.io.IOException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "database", matchIfMissing = true)
public class DatabaseOriginalDocumentStorage implements OriginalDocumentStorage {
    @Override
    public StoredDocument store(DocumentEntity document, MultipartFile file) throws IOException {
        return new StoredDocument(null, file.getSize());
    }
}
