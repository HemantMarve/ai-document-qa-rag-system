package com.marvetech.ai.rag;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "app.jwt.secret=test-secret-test-secret-test-secret-123456",
    "spring.datasource.url=jdbc:h2:mem:ragtest;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
class DocumentQaRagApplicationTests {
    @Test
    void contextLoads() {
    }
}
