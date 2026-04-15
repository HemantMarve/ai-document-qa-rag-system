package com.marvetech.ai.rag.document;

import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public DocumentResponse upload(@RequestPart("file") @NotNull MultipartFile file) throws IOException {
        return documentService.upload(file);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<DocumentResponse> list() {
        return documentService.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public DocumentResponse get(@PathVariable UUID id) {
        return documentService.get(id);
    }
}
