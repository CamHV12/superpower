package com.company.enterprise.audit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import com.company.enterprise.security.JwtAuthenticationFilter;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuditLogController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class AuditLogControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean AuditLogService service;

    @Test
    void returnsAuditLogs() throws Exception {
        AuditLog log = new AuditLog(null, "admin@example.com", "GET",
                "/api/v1/dashboard/overview", 200, 12, "127.0.0.1", "JUnit");
        when(service.findAll(any())).thenReturn(new PageImpl<>(List.of(AuditLogResponse.from(log)), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/audit-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].actorEmail").value("admin@example.com"))
                .andExpect(jsonPath("$.content[0].action").value("GET"))
                .andExpect(jsonPath("$.content[0].statusCode").value(200));
    }
}
