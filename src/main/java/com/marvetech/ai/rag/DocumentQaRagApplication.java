package com.marvetech.ai.rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class DocumentQaRagApplication {
    public static void main(String[] args) {
        SpringApplication.run(DocumentQaRagApplication.class, args);
    }
}
