package com.company.enterprise.admin.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserAdminResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        boolean enabled,
        boolean locked,
        Instant lockedUntil,
        int failedLoginAttempts,
        Set<String> roles,
        Instant createdAt,
        Instant updatedAt
) {}