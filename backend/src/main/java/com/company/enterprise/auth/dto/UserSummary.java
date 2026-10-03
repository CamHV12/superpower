package com.company.enterprise.auth.dto;

import java.util.List;
import java.util.UUID;

public record UserSummary(
        UUID id,
        String email,
        String firstName,
        String lastName,
        List<String> roles
) {}