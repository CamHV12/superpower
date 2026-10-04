package com.company.enterprise.audit;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuditLoggingFilter extends OncePerRequestFilter {
    private final ObjectProvider<AuditLogService> serviceProvider;

    public AuditLoggingFilter(ObjectProvider<AuditLogService> serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        long started = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String actorEmail = authentication != null && authentication.isAuthenticated()
                        ? authentication.getName() : null;

                AuditLogService service = serviceProvider.getIfAvailable();
                if (service == null) return;
                service.record(new AuditLog(
                        null,
                        actorEmail,
                        request.getMethod(),
                        request.getRequestURI(),
                        response.getStatus(),
                        (System.nanoTime() - started) / 1_000_000,
                        resolveClientIp(request),
                        truncate(request.getHeader("User-Agent"), 1000)
                ));
            } catch (RuntimeException ignored) {
                // Audit logging must never make a business request fail.
            }
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return truncate(forwarded.split(",")[0].trim(), 100);
        }
        return truncate(request.getRemoteAddr(), 100);
    }

    private String truncate(String value, int max) {
        if (value == null) return null;
        return value.length() <= max ? value : value.substring(0, max);
    }
}