package com.company.enterprise.dashboard;

import java.time.Instant;
import java.util.UUID;

public record DashboardActivity(
        UUID id,
        String type,
        String title,
        String description,
        Instant occurredAt
) {}
