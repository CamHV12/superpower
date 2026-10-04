package com.company.enterprise.audit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogTest {

    @Test
    void createsAuditLogWithRequestMetadata() {
        AuditLog log = new AuditLog(
                null,
                "admin@example.com",
                "POST",
                "/api/v1/projects",
                201,
                42,
                "127.0.0.1",
                "JUnit");

        assertNotNull(log.getId());
        assertEquals("admin@example.com", log.getActorEmail());
        assertEquals("POST", log.getAction());
        assertEquals("/api/v1/projects", log.getPath());
        assertEquals(201, log.getStatusCode());
        assertEquals(42, log.getDurationMs());
        assertEquals("127.0.0.1", log.getIpAddress());
        assertEquals("JUnit", log.getUserAgent());
        assertNotNull(log.getCreatedAt());
    }
}