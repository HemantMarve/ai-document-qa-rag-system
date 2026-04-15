package com.marvetech.ai.rag.document;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TextExtractionService {
    public String extract(MultipartFile file) throws IOException {
        var contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        var filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (contentType.contains("pdf") || filename.endsWith(".pdf")) {
            try (var document = Loader.loadPDF(file.getBytes())) {
                return new PDFTextStripper().getText(document);
            }
        }
        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }
}
