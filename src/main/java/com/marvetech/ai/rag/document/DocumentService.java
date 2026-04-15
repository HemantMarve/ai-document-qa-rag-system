package com.marvetech.ai.rag.document;

import com.marvetech.ai.rag.shared.CurrentUser;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final TextExtractionService textExtractionService;
    private final OriginalDocumentStorage originalDocumentStorage;
    private final IngestionDispatcher ingestionDispatcher;
    private final CurrentUser currentUser;

    public DocumentService(DocumentRepository documentRepository, TextExtractionService textExtractionService, OriginalDocumentStorage originalDocumentStorage, IngestionDispatcher ingestionDispatcher, CurrentUser currentUser) {
        this.documentRepository = documentRepository;
        this.textExtractionService = textExtractionService;
        this.originalDocumentStorage = originalDocumentStorage;
        this.ingestionDispatcher = ingestionDispatcher;
        this.currentUser = currentUser;
    }

    public DocumentResponse upload(MultipartFile file) throws IOException {
        var extractedText = textExtractionService.extract(file);
        if (extractedText.isBlank()) {
            throw new IllegalArgumentException("Document does not contain extractable text");
        }
        var document = new DocumentEntity();
        document.setTenantId(currentUser.tenantId());
        document.setOwnerUsername(currentUser.username());
        document.setFilename(file.getOriginalFilename() == null ? "document" : file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setRawText(extractedText);
        var saved = documentRepository.save(document);
        var storedDocument = originalDocumentStorage.store(saved, file);
        saved.setObjectKey(storedDocument.objectKey());
        saved.setSizeBytes(storedDocument.sizeBytes());
        saved = documentRepository.save(saved);
        ingestionDispatcher.dispatch(saved.getId(), saved.getTenantId());
        return DocumentResponse.from(saved);
    }

    public List<DocumentResponse> list() {
        return documentRepository.findByTenantIdOrderByCreatedAtDesc(currentUser.tenantId()).stream().map(DocumentResponse::from).toList();
    }

    public DocumentResponse get(UUID id) {
        return documentRepository.findByIdAndTenantId(id, currentUser.tenantId()).map(DocumentResponse::from).orElseThrow();
    }
}
