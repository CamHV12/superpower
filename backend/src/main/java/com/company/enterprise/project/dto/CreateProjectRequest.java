package com.company.enterprise.project.dto;

import com.company.enterprise.project.entity.ProjectPriority;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateProjectRequest(
        @NotBlank @Size(max = 50) String code,
        @NotBlank @Size(max = 200) String name,
        @Size(max = 2000) String description,
        @NotNull UUID managerId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @DecimalMin(value = "0.0") BigDecimal budget,
        @NotNull ProjectPriority priority
) {}
