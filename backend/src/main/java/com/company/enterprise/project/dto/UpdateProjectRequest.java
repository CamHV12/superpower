package com.company.enterprise.project.dto;

import com.company.enterprise.project.entity.ProjectPriority;
import com.company.enterprise.project.entity.ProjectStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateProjectRequest(
        @Size(max = 200) String name,
        @Size(max = 2000) String description,
        @NotNull UUID managerId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @DecimalMin(value = "0.0") BigDecimal budget,
        @NotNull ProjectStatus status,
        @NotNull ProjectPriority priority,
        @NotNull Integer progress
) {}
