package com.marvetech.ai.rag.document;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface OriginalDocumentStorage {
    StoredDocument store(DocumentEntity document, MultipartFile file) throws IOException;
}
