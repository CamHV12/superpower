package com.company.enterprise.audit;

import com.company.enterprise.security.EnterpriseUserPrincipal;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class AuditLoggingFilterTest {
    @Test
    void recordsAuthenticatedUserIdAndRequestMetadata() throws Exception {
        AuditLogService service = mock(AuditLogService.class);
        ObjectProvider<AuditLogService> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(service);

        UUID userId = UUID.randomUUID();
        var principal = new EnterpriseUserPrincipal(
                userId, "admin@example.com", "hash", true,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/audit-logs");
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("User-Agent", "JUnit");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> ((MockHttpServletResponse) res).setStatus(200);

        try {
            new AuditLoggingFilter(provider).doFilter(request, response, chain);

            var captor = org.mockito.ArgumentCaptor.forClass(AuditLog.class);
            verify(service).record(captor.capture());
            AuditLog log = captor.getValue();
            org.junit.jupiter.api.Assertions.assertEquals(userId, log.getActorUserId());
            org.junit.jupiter.api.Assertions.assertEquals("admin@example.com", log.getActorEmail());
            org.junit.jupiter.api.Assertions.assertEquals("GET", log.getAction());
            org.junit.jupiter.api.Assertions.assertEquals("/api/v1/audit-logs", log.getPath());
            org.junit.jupiter.api.Assertions.assertEquals(200, log.getStatusCode());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
