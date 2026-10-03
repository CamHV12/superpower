package com.company.enterprise.project.dto;

import com.company.enterprise.project.entity.ProjectPriority;
import com.company.enterprise.project.entity.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String code,
        String name,
        String description,
        UUID managerId,
        String managerName,
        ProjectStatus status,
        ProjectPriority priority,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal budget,
        int progress,
        Instant createdAt,
        Instant updatedAt
) {}
