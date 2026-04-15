package com.marvetech.ai.rag.document;

import com.marvetech.ai.rag.config.AppProperties;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ChunkingService {
    private final int chunkSize;
    private final int overlap;

    public ChunkingService(AppProperties properties) {
        this.chunkSize = properties.getRag().getChunkSize();
        this.overlap = Math.min(properties.getRag().getChunkOverlap(), chunkSize - 1);
    }

    public List<String> chunk(String text) {
        var normalized = text == null ? "" : text.replaceAll("\\s+", " ").trim();
        var chunks = new ArrayList<String>();
        if (normalized.isBlank()) {
            return chunks;
        }
        int start = 0;
        while (start < normalized.length()) {
            int end = Math.min(start + chunkSize, normalized.length());
            chunks.add(normalized.substring(start, end).trim());
            if (end == normalized.length()) {
                break;
            }
            start = Math.max(0, end - overlap);
        }
        return chunks;
    }
}
